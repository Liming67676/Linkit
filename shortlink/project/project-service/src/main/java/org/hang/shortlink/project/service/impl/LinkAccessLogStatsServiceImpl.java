package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsTopIpResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsUvResponseDto;
import org.hang.shortlink.project.mapper.LinkMapper;
import org.hang.shortlink.project.pojo.dto.GaoDeIpLocationDTO;
import org.hang.shortlink.project.pojo.po.LinkAccessLogStats;
import org.hang.shortlink.project.mapper.LinkAccessLogStatsMapper;
import org.hang.shortlink.project.service.ILinkAccessLogStatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.project.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 短链接全维度访问日志统计表 服务实现类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkAccessLogStatsServiceImpl extends ServiceImpl<LinkAccessLogStatsMapper, LinkAccessLogStats> implements ILinkAccessLogStatsService {



    // 热数据天数：30天
    private static final int HOT_DATA_DAYS = 30;

    private static final String UNKNOWN = "未知";
    private final IpUtils ipUtils;

    private final LinkMapper linkMapper;

    private final StringRedisTemplate stringRedisTemplate;

    // 列表页去重访客缓存：日志表按 full_short_url 分片，一次查询要扇出多个分片，命中缓存可省掉整次往返
    private static final String VISITOR_CACHE_PREFIX = "short-link:stats:visitor:";

    private static final long VISITOR_CACHE_SECONDS = 120L;

    /**
     * 逐链接并行查询的上限，超过就退回一次 IN 查询，避免一次请求投放太多任务
     */
    private static final int VISITOR_PARALLEL_LIMIT = 24;


    @Override
    public void insertAccessLog(String fullShortUrl, String ip, String osName
    ,String browser,String device,String network) {
        try {
            GaoDeIpLocationDTO location = ipUtils.getLocationByIp(ip);

            // 规则：同一个短链接 + 同一个IP，第一次访问 = newUser，后续 = oldUser
            boolean isNewVisitor = isNewVisitor(fullShortUrl, ip);
            String uvType = isNewVisitor ? "newUser" : "oldUser";

            LinkAccessLogStats log = LinkAccessLogStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .ip(ip)
                    .user(ip) // 无登录用户，用IP作为用户标识
                    .country(location != null ? "中国" : UNKNOWN)
                    .province(location != null ? location.getProvince() : UNKNOWN)
                    .city(location != null ? location.getCity() : UNKNOWN)
                    .adcode(location != null ? location.getAdcode() : UNKNOWN)
                    .os(osName)
                    .browser(browser)
                    .device(device)
                    .network(network)
                    .uvType(uvType) // 赋值新老访客
                    .createTime(LocalDateTime.now())
                    .build();
            save(log);
        } catch (Exception e) {
            log.error("插入访问日志失败", e);
        }
    }

    /**
     * 判断是否为新访客（同一短链接 + 同一IP 是否在近30天内访问过）
     * 限定30天时间窗口，与热数据策略一致，避免历史数据被清理后误判
     */
    private boolean isNewVisitor(String fullShortUrl, String ip) {
        LocalDateTime hotStart = LocalDateTime.now().minusDays(HOT_DATA_DAYS);
        return lambdaQuery()
                .eq(LinkAccessLogStats::getFullShortUrl, fullShortUrl)
                .eq(LinkAccessLogStats::getIp, ip)
                .ge(LinkAccessLogStats::getCreateTime, hotStart)
                .count() == 0;
    }

    @Override
    public List<ShortLinkStatsTopIpResponseDto> getTopIpStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        // 强制校验热数据
        checkHotData(start);
        List<Map<String, Object>> ipList = baseMapper.selectTopIpStats(fullShortUrl, start, end);
        if (ipList.isEmpty()) {
            return Collections.emptyList();
        }
        return ipList.stream()
                .map(map -> ShortLinkStatsTopIpResponseDto.builder()
                        .ip((String) map.get("ip"))
                        .cnt(((Number) map.get("cnt")).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsUvResponseDto> getUvTypeStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        // 强制校验热数据（根据你的业务需求保留）
        checkHotData(start);
        List<ShortLinkStatsUvResponseDto> uvTypeStats = new ArrayList<>();
        // 查询新老访客
        Map<String, Object> findUvTypeByShortLink = baseMapper.findUvTypeCntByShortLink(fullShortUrl, start, end);

        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLink)
                        .map(each -> each.get("oldUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLink)
                        .map(each -> each.get("newUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );

        // 计算占比（已处理uvSum=0的情况）
        int uvSum = oldUserCnt + newUserCnt;
        double actualOldRatio = 0.0;
        double actualNewRatio = 0.0;
        if (uvSum > 0) {
            actualOldRatio = Math.round((double) oldUserCnt / uvSum * 10000.0) / 100.0;
            actualNewRatio = Math.round((double) newUserCnt / uvSum * 10000.0) / 100.0;
        }

        // 封装DTO
        uvTypeStats.add(ShortLinkStatsUvResponseDto.builder().uvType("newUser").cnt(newUserCnt).ratio(actualNewRatio).build());
        uvTypeStats.add(ShortLinkStatsUvResponseDto.builder().uvType("oldUser").cnt(oldUserCnt).ratio(actualOldRatio).build());

        return uvTypeStats;
    }

    @Override
    public List<ShortLinkStatsTopIpResponseDto> getTopIpStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        // 强制校验热数据
        checkHotData(start);
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> ipList = baseMapper.selectTopIpStatsBatch(fullShortUrls, start, end);
        if (ipList.isEmpty()) return Collections.emptyList();
        return ipList.stream()
                .map(map -> ShortLinkStatsTopIpResponseDto.builder()
                        .ip((String) map.get("ip"))
                        .cnt(((Number) map.get("cnt")).intValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsUvResponseDto> getUvTypeStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        // 强制校验热数据
        checkHotData(start);
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        log.info("[getUvTypeStatsBatch] 查询参数 - fullShortUrls={}, start={}, end={}", fullShortUrls, start, end);
        List<ShortLinkStatsUvResponseDto> uvTypeStats = new ArrayList<>();
        // 查询新老访客
        Map<String, Object> findUvTypeByShortLinks = baseMapper.findUvTypeCntByShortLinks(fullShortUrls, start, end);

        int oldUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLinks)
                        .map(each -> each.get("oldUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );
        int newUserCnt = Integer.parseInt(
                Optional.ofNullable(findUvTypeByShortLinks)
                        .map(each -> each.get("newUserCnt"))
                        .map(Object::toString)
                        .orElse("0")
        );

        // 计算占比
        int uvSum = oldUserCnt + newUserCnt;
        double actualOldRatio = 0.0;
        double actualNewRatio = 0.0;
        if (uvSum > 0) {
            actualOldRatio = Math.round((double) oldUserCnt / uvSum * 10000.0) / 100.0;
            actualNewRatio = Math.round((double) newUserCnt / uvSum * 10000.0) / 100.0;
        }

        // 封装DTO
        uvTypeStats.add(ShortLinkStatsUvResponseDto.builder().uvType("newUser").cnt(newUserCnt).ratio(actualNewRatio).build());
        uvTypeStats.add(ShortLinkStatsUvResponseDto.builder().uvType("oldUser").cnt(oldUserCnt).ratio(actualOldRatio).build());

        return uvTypeStats;
    }

    @Override
    public Map<String, Integer> countDistinctVisitor(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        checkHotData(start);
        return toVisitorCount(baseMapper.countDistinctVisitor(fullShortUrl, start, end));
    }

    @Override
    public Map<String, Integer> countDistinctVisitorBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        checkHotData(start);
        if (CollUtil.isEmpty(fullShortUrls)) {
            return Map.of("uv", 0, "uip", 0);
        }
        // 跨分片 COUNT(DISTINCT) 的合并结果不可靠，改为取明细行在内存里用 Set 去重
        List<Map<String, Object>> rows = baseMapper.countDistinctVisitorBatch(fullShortUrls, start, end);
        return countVisitorFromRows(rows);
    }

    private Map<String, Integer> countVisitorFromRows(List<Map<String, Object>> rows) {
        if (CollUtil.isEmpty(rows)) {
            return Map.of("uv", 0, "uip", 0);
        }
        Set<String> users = new HashSet<>();
        Set<String> ips = new HashSet<>();
        for (Map<String, Object> row : rows) {
            Object user = row.get("user");
            Object ip = row.get("ip");
            users.add(user == null ? "" : user.toString());
            ips.add(ip == null ? "" : ip.toString());
        }
        return Map.of("uv", users.size(), "uip", ips.size());
    }

    /**
     * 统一把聚合结果转成 uv/uip：分片合并后返回值可能是字符串，统一按字符串解析
     */
    private Map<String, Integer> toVisitorCount(Map<String, Object> row) {
        if (row == null || row.isEmpty()) {
            return Map.of("uv", 0, "uip", 0);
        }
        return Map.of("uv", toInt(row.get("uv")), "uip", toInt(row.get("uip")));
    }

    private int toInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public Map<String, Map<String, Integer>> getPageDistinctVisitor(List<String> fullShortUrls) {
        if (CollUtil.isEmpty(fullShortUrls)) {
            return Collections.emptyMap();
        }
        List<String> urls = fullShortUrls.stream().filter(Objects::nonNull).distinct().toList();
        if (urls.isEmpty()) {
            return Collections.emptyMap();
        }

        // Redis 同样在隧道另一端，必须一次 MGET 取全部，绝不能逐个 GET
        Map<String, Integer[]> cached = readVisitorCacheBatch(urls);
        Map<String, Map<String, Integer>> result = new HashMap<>(urls.size());
        List<String> missUrls = new ArrayList<>();
        for (String fullShortUrl : urls) {
            Integer[] hit = cached.get(fullShortUrl);
            if (hit != null) {
                result.put(fullShortUrl, Map.of("uv", hit[0], "uip", hit[1]));
            } else {
                missUrls.add(fullShortUrl);
            }
        }
        if (missUrls.isEmpty()) {
            return result;
        }

        Map<String, Integer[]> fresh = new HashMap<>(missUrls.size());
        if (missUrls.size() <= VISITOR_PARALLEL_LIMIT) {
            fillBySingleUrlQueries(missUrls, result, fresh);
        } else {
            fillByBatchQuery(missUrls, result, fresh);
        }
        // 回填缓存也用 pipeline，一次往返写完
        writeVisitorCacheBatch(fresh);
        return result;
    }

    /**
     * 逐链接查询：每个链接只落在一个分片上，COUNT(DISTINCT) 在库内算完，
     * 一次往返只带回一行，不用把明细行拉回应用；并行执行把 N 条链接压成 N/线程数 批。
     */
    private void fillBySingleUrlQueries(List<String> missUrls, Map<String, Map<String, Integer>> result,
                                        Map<String, Integer[]> fresh) {
        for (String fullShortUrl : missUrls) {
            Map<String, Integer> count = toSingleVisitorCount(baseMapper.selectVisitorCount(fullShortUrl));
            result.put(fullShortUrl, count);
            fresh.put(fullShortUrl, new Integer[]{count.get("uv"), count.get("uip")});
        }
    }

    /**
     * 日志表里 user 字段就是 ip（无登录体系，用 IP 作为访客标识），一次去重即可得到 uv/uip
     */
    private Map<String, Integer> toSingleVisitorCount(Map<String, Object> row) {
        int count = row == null ? 0 : toInt(row.get("cnt"));
        return Map.of("uv", count, "uip", count);
    }

    /**
     * 链接较多时退回一次 IN 查询：分片扇出由 ShardingSphere 负责，
     * 避免一次请求向线程池投放几十个任务。
     */
    private void fillByBatchQuery(List<String> missUrls, Map<String, Map<String, Integer>> result,
                                  Map<String, Integer[]> fresh) {
        Map<String, Set<String>> uvSets = new HashMap<>();
        Map<String, Set<String>> uipSets = new HashMap<>();
        for (Map<String, Object> row : baseMapper.selectPageDistinctVisitor(missUrls)) {
            String fullShortUrl = (String) row.get("full_short_url");
            if (fullShortUrl == null) {
                continue;
            }
            Object user = row.get("user");
            Object ip = row.get("ip");
            uvSets.computeIfAbsent(fullShortUrl, k -> new HashSet<>()).add(user == null ? "" : user.toString());
            uipSets.computeIfAbsent(fullShortUrl, k -> new HashSet<>()).add(ip == null ? "" : ip.toString());
        }
        for (String fullShortUrl : missUrls) {
            int uv = uvSets.getOrDefault(fullShortUrl, Collections.emptySet()).size();
            int uip = uipSets.getOrDefault(fullShortUrl, Collections.emptySet()).size();
            result.put(fullShortUrl, Map.of("uv", uv, "uip", uip));
            fresh.put(fullShortUrl, new Integer[]{uv, uip});
        }
    }

    /**
     * 批量读缓存：一次 MGET，失败一律当作未命中，保证 Redis 异常时接口仍可用
     */
    private Map<String, Integer[]> readVisitorCacheBatch(List<String> urls) {
        try {
            List<String> keys = urls.stream().map(url -> VISITOR_CACHE_PREFIX + url).toList();
            List<String> values = stringRedisTemplate.opsForValue().multiGet(keys);
            if (values == null || values.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, Integer[]> result = new HashMap<>(urls.size());
            for (int i = 0; i < urls.size() && i < values.size(); i++) {
                Integer[] parsed = parseVisitorValue(values.get(i));
                if (parsed != null) {
                    result.put(urls.get(i), parsed);
                }
            }
            return result;
        } catch (Exception e) {
            log.warn("[去重访客] 批量读取缓存失败，全部回源查询", e);
            return Collections.emptyMap();
        }
    }

    private void writeVisitorCacheBatch(Map<String, Integer[]> counts) {
        if (counts.isEmpty()) {
            return;
        }
        try {
            stringRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Map.Entry<String, Integer[]> entry : counts.entrySet()) {
                    byte[] key = (VISITOR_CACHE_PREFIX + entry.getKey()).getBytes(StandardCharsets.UTF_8);
                    byte[] value = (entry.getValue()[0] + ":" + entry.getValue()[1]).getBytes(StandardCharsets.UTF_8);
                    connection.setEx(key, VISITOR_CACHE_SECONDS, value);
                }
                return null;
            });
        } catch (Exception e) {
            log.warn("[去重访客] 批量写入缓存失败，忽略本次缓存", e);
        }
    }

    private Integer[] parseVisitorValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split(":");
        if (parts.length != 2) {
            return null;
        }
        try {
            return new Integer[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public PageResult<ShortLinkStatsAccessRecordResponseDto> getAccessRecordPage(ShortLinkStatsAccessRecordRequestDto request) {
        // 1. 提取参数
        String fullShortUrl = request.getFullShortUrl();
        String gid = request.getGid();
        Long current = request.getCurrent();
        Long size = request.getSize();
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();

        // 2. 分页参数默认值
        current = (current == null || current < 1) ? 1L : current;
        size = (size == null || size < 1) ? 10L : size;

        // 3. 热数据强制校验（自动修复超范围时间）
        LocalDateTime hotDate = LocalDateTime.now().minusDays(HOT_DATA_DAYS);
        startDate = (startDate == null || startDate.isBefore(hotDate)) ? hotDate : startDate;
        endDate = (endDate == null) ? LocalDateTime.now() : endDate;

        // 4. 获取待查询的短链接集合（GID 优先 / 单个链接次之）
        List<String> fullShortUrls = getQueryShortUrls(gid, fullShortUrl);
        if (fullShortUrls.isEmpty()) {
            return new PageResult<>(size, current, 0L, 0L,Collections.emptyList());
        }

        // 5. 分页查询数据库
        Page<LinkAccessLogStats> page = new Page<>(current, size);
        IPage<LinkAccessLogStats> resultPage = baseMapper.selectAccessRecordPage(page, fullShortUrls, startDate, endDate);


        // 7. DTO 转换（动态设置 uv_type）
        List<ShortLinkStatsAccessRecordResponseDto> records = resultPage.getRecords().stream()
                .map(log -> ShortLinkStatsAccessRecordResponseDto.builder()
                        .uvType(log.getUvType())
                        .browser(log.getBrowser())
                        .os(log.getOs())
                        .ip(log.getIp())
                        .network(log.getNetwork())
                        .device(log.getDevice())
                        .locale(log.getProvince())
                        .user(log.getUser())
                        .createTime(java.sql.Timestamp.valueOf(log.getCreateTime()))
                        .build())
                .toList();

        return new PageResult<>(resultPage.getSize(),resultPage.getCurrent(),  resultPage.getPages(),resultPage.getTotal(), records);
    }

    /**
     * 获取需要查询的短链接列表（解耦：分组GID → 批量fullShortUrl）
     */
    private List<String> getQueryShortUrls(String gid, String fullShortUrl) {
        if (fullShortUrl != null && !fullShortUrl.isBlank()) {
            return List.of(fullShortUrl);
        }
        if (gid != null && !gid.isBlank()) {
            return linkMapper.selectFullShortUrlsByGid(gid);
        }
        return new ArrayList<>();
    }


    // 工具方法：校验是否为热数据查询
    private void checkHotData(LocalDateTime start) {
        LocalDateTime hotDate = LocalDateTime.now().minusDays(HOT_DATA_DAYS);
        if (start.isBefore(hotDate)) {
            throw new ServiceException("仅支持查询最近" + HOT_DATA_DAYS + "天的访问日志");
        }
    }
}
