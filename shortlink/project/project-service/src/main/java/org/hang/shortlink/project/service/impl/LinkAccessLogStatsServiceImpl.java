package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.*;
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
        log.info("[getUvTypeStats] 查询参数 - fullShortUrl={}, start={}, end={}", fullShortUrl, start, end);
        List<ShortLinkStatsUvResponseDto> uvTypeStats = new ArrayList<>();
        // 查询新老访客
        Map<String, Object> findUvTypeByShortLink = baseMapper.findUvTypeCntByShortLink(fullShortUrl, start, end);
        log.info("[getUvTypeStats] 查询结果 - {}", findUvTypeByShortLink);

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
        log.info("[getUvTypeStatsBatch] 查询结果 - {}", findUvTypeByShortLinks);

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
