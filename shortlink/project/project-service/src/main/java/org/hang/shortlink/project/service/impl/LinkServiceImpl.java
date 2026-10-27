package org.hang.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.constant.RedisConstant;
import org.hang.shortlink.project.dto.link.LinkBaseInfo;
import org.hang.shortlink.project.dto.link.request.LinkCreateRequestDto;
import org.hang.shortlink.project.dto.link.request.LinkPageQueryDto;
import org.hang.shortlink.project.dto.link.request.LinkUpdateRequestDto;
import org.hang.shortlink.project.dto.link.request.ShortLinkBatchCreateRequestDto;
import org.hang.shortlink.project.dto.link.reseponse.LikCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkBatchCreateResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkGroupCountResponseDto;
import org.hang.shortlink.project.dto.link.reseponse.LinkPageResponseDto;
import org.hang.shortlink.project.pojo.po.Link;
import org.hang.shortlink.project.mapper.LinkAccessStatsMapper;
import org.hang.shortlink.project.mapper.LinkMapper;
import org.hang.shortlink.project.pojo.po.LinkGoto;
import org.hang.shortlink.project.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.project.utils.*;
import org.hang.shortlink.project.mq.dto.FaviconMessage;
import org.hang.shortlink.project.mq.dto.UrlSecurityMessage;
import org.hang.shortlink.project.mq.dto.StatsMessage;
import org.hang.shortlink.project.mq.producer.RabbitMQProducer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhanghang1216
 * @since 2026-04-21
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements ILinkService {

    private static final int TITLE_PROBE_TIMEOUT_MS = 1200;

    private static final int TITLE_FETCH_TIMEOUT_MS = 2500;

    private static final int TITLE_MAX_BODY_SIZE = 256 * 1024;

    private static final String TITLE_CACHE_PREFIX = "short-link:title:";

    private static final String EMPTY_TITLE_CACHE_VALUE = "__EMPTY__";

    private static final String UNREACHABLE_TITLE_CACHE_VALUE = "__TITLE_NOT_EXISTS__";

    private static final long TITLE_CACHE_TTL_SECONDS = 30L;

    private static final long SHORT_LINK_GOTO_CACHE_TTL_SECONDS = 5 * 60L;

    private static final int FAVICON_FETCH_TIMEOUT_MS = 2000;

    private static final int FAVICON_CHECK_TIMEOUT_MS = 1000;

    private static final int FAVICON_MAX_BODY_SIZE = 256 * 1024;

    private static final String FAVICON_CACHE_PREFIX = "short-link:favicon:";

    private static final String DEFAULT_FAVICON_URL = "https://img.icons8.com/fluency/32/000000/link.png";

    private static final long FAVICON_CACHE_TTL_SECONDS = 30L;

    private static final long SLOW_CREATE_LINK_MILLIS = 800L;

    private final ShortUrlGeneratorUtils shortUrlGeneratorUtils;

    private final RedissonClient redissonClient;

    private final ILinkGotoService linkGotoService;

    private final StringRedisTemplate redisTemplate;

    private final RBloomFilter<String> shortLinkBloomFilter;

    private final ILinkAccessStatsService linkAccessStatsService;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    private final ILinkAccessLogStatsService linkAccessLogStatsService;

    private final IpUtils ipUtils; // IP工具类



    private final UrlSecurityCheckHandler urlSecurityCheckHandler;

    private final RabbitMQProducer rabbitMQProducer;

    @Transactional
    @Override
    public LikCreateResponseDto createLink(LinkCreateRequestDto dto) {
        long startTime = System.currentTimeMillis();
        long securityCheckCost = 0L;
        long duplicateCheckCost = 0L;
        long saveCost = 0L;
        long mqCost = 0L;
        long cacheDeleteCost = 0L;
        if (dto == null) {
            throw new ServiceException("创建短链接参数不足");
        }

        // 创建短链接
        if (!StringUtils.hasText(dto.getDomain()) || !StringUtils.hasText(dto.getOriginUrl())) {
            throw new ServiceException("创建短链接参数不足");
        }
        // 排除不是http:// 或https:// 开头的短链接
        if(!dto.getOriginUrl().startsWith("http://") && !dto.getOriginUrl().startsWith("https://")){
            throw new ServiceException("请输入http://或者https://开头的短链接");
        }

        // URL安全检查（黑名单/内容合规）
        long securityCheckStartTime = System.currentTimeMillis();
        urlSecurityCheckHandler.checkUrl(dto.getOriginUrl());
        securityCheckCost = elapsedMillis(securityCheckStartTime);

        // 同一个分组只能有唯一一个源地址
        String lockKey = "link:create:" + dto.getGid() + ":" + dto.getOriginUrl();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(); // 阻塞锁
        try {
            long duplicateCheckStartTime = System.currentTimeMillis();
            Long count = lambdaQuery().eq(Link::getGid, dto.getGid())
                    .eq(Link::getOriginUrl, dto.getOriginUrl())
                    .count();
            duplicateCheckCost = elapsedMillis(duplicateCheckStartTime);
            if (!count.equals(0L)) {
                throw new ServiceException("该分组下已存在此原始链接，请勿重复创建！");
            }
            String domain = "https://" + dto.getDomain();

            // 生成短链接
            String shorUri = shortUrlGeneratorUtils.generateShortCode(domain, dto.getOriginUrl());
            Link link = Link.builder().domain(domain)
                    .shortUri(shorUri)
                    .fullShortUrl(domain + "/" + shorUri)
                    .originUrl(dto.getOriginUrl())
                    .gid(dto.getGid())
                    .clickNum(0)
                    .enableStatus(0) // 默认启用
                    .createdType(dto.getCreatedType() != null ? dto.getCreatedType() : 1) // 创建类型
                    .validDateType(dto.getValidDateType() != null ? dto.getValidDateType() : 0) //有效期类型
                    .describe(dto.getDescribe())
                    .favicon(null)
                    .build();
            // 有效时间
            if (dto.getValidDateType() != null && dto.getValidDateType().equals(1)) {
                link.setValidDate(dto.getValidDate());
            } else {
                link.setValidDate(LocalDateTime.now().plusDays(30));// 默认30天有效期
            }
            long saveStartTime = System.currentTimeMillis();
            boolean saved = save(link);
            saveCost = elapsedMillis(saveStartTime);
            if (!saved) {
                throw new ServiceException("新增当个短链接失败");
            }
            // 同步写入跳转关系，辅助任务等事务提交后再发送
            long mqStartTime = System.currentTimeMillis();
            saveLinkGoto(link.getFullShortUrl(), link.getGid());
            sendAuxiliaryMessagesAfterCommit(link.getFullShortUrl(), link.getOriginUrl(), link.getGid());
            mqCost = elapsedMillis(mqStartTime);
            long cacheDeleteStartTime = System.currentTimeMillis();
            deleteGroupCountCache(link.getGid());
            cacheDeleteCost = elapsedMillis(cacheDeleteStartTime);
            long totalCost = elapsedMillis(startTime);
            if (totalCost >= SLOW_CREATE_LINK_MILLIS) {
                log.info("create short link slow originUrl={}, gid={}, totalCostMs={}, securityCheckMs={}, duplicateCheckMs={}, saveMs={}, mqMs={}, cacheDeleteMs={}",
                        dto.getOriginUrl(), dto.getGid(), totalCost, securityCheckCost, duplicateCheckCost, saveCost, mqCost, cacheDeleteCost);
            }
            return new LikCreateResponseDto(link.getGid(), link.getOriginUrl(), link.getFullShortUrl());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LinkBatchCreateResponseDto batchCreateShortLink(ShortLinkBatchCreateRequestDto requestParam) {
        // 1. 基础入参校验
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        String gid = requestParam.getGid();
        if (CollUtil.isEmpty(originUrls) || CollUtil.isEmpty(describes) || originUrls.size() != describes.size()) {
            throw new ServiceException("批量创建参数异常");
        }

        // 2. 构建批量参数对象
        List<LinkCreateRequestDto> requestDtoList = new ArrayList<>();
        Set<String> checkedOriginUrls = new HashSet<>();
        for (int i = 0; i < originUrls.size(); i++) {
            // 排除不是http:// 或https:// 开头的短链接
            if(!originUrls.get(i).startsWith("http://") && !originUrls.get(i).startsWith("https://")){
                throw new ServiceException("请输入http://或者https://开头的短链接");
            }
            String originUrl = originUrls.get(i);
            if (checkedOriginUrls.add(originUrl)) {
                urlSecurityCheckHandler.checkUrl(originUrl);
            }
            String describe = describes.get(i);
            if (StrUtil.isBlank(originUrl)) {
                log.error("原始链接为空，跳过创建：索引{}", i);
                continue;
            }
            LinkCreateRequestDto dto = new LinkCreateRequestDto(
                    requestParam.getDomain(),
                    requestParam.getCreatedType(),
                    requestParam.getValidDateType(),
                    requestParam.getValidDate(),
                    describe,
                    originUrl,
                    gid
            );
            requestDtoList.add(dto);
        }
        if (CollUtil.isEmpty(requestDtoList)) {
            return LinkBatchCreateResponseDto.builder().total(0).baseLinkInfos(Collections.emptyList()).build();
        }

        // 3. 分布式锁：分组级锁（优化锁粒度，比单条锁高效）
        String lockKey = "link:batch:create:" + gid;
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();

        try {
            // 4. 批量校验重复：1次查询，判断该分组下所有已存在的原始链接
            Set<String> existOriginUrlSet = getExistOriginUrlSet(gid, requestDtoList);

            // 5. 过滤重复数据，只处理新增链接
            List<LinkCreateRequestDto> needCreateList = requestDtoList.stream()
                    .filter(dto -> !existOriginUrlSet.contains(dto.getOriginUrl()))
                    .collect(Collectors.collectingAndThen(
                            Collectors.toMap(
                                    LinkCreateRequestDto::getOriginUrl,
                                    dto -> dto,
                                    (first, second) -> first
                            ),
                            map -> new ArrayList<>(map.values())
                    ));

            if (CollUtil.isEmpty(needCreateList)) {
                log.info("批量创建：所有链接均已存在，gid:{}", gid);
                return LinkBatchCreateResponseDto.builder().total(0).baseLinkInfos(Collections.emptyList()).build();
            }

            // 6. 批量生成短链接实体
            List<Link> linkList = buildBatchLinkList(needCreateList);
            if (CollUtil.isEmpty(linkList)) {
                return LinkBatchCreateResponseDto.builder().total(0).baseLinkInfos(Collections.emptyList()).build();
            }

            // 7.  批量插入数据库
            boolean saveBatchResult = saveBatch(linkList);
            if (!saveBatchResult) {
                throw new ServiceException("批量创建短链接失败");
            }

            // 8. 封装返回结果
            List<LinkBaseInfo> baseInfoList = linkList.stream()
                    .map(link -> new LinkBaseInfo(link.getGid(), link.getOriginUrl(), link.getFullShortUrl()))
                    .collect(Collectors.toList());

            // 9. 同步写入LinkGoto，事务提交后异步获取favicon和URL安全检测
            for (Link link : linkList) {
                saveLinkGoto(link.getFullShortUrl(), link.getGid());
                sendAuxiliaryMessagesAfterCommit(link.getFullShortUrl(), link.getOriginUrl(), link.getGid());
            }
            deleteGroupCountCache(gid);

            return LinkBatchCreateResponseDto.builder()
                    .total(baseInfoList.size())
                    .baseLinkInfos(baseInfoList)
                    .build();

        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLink(LinkUpdateRequestDto dto) {
        if (dto == null) {
            throw new ServiceException("短链接更新失败");
        }
        // 排除不是http:// 或https:// 开头的短链接
        if(!dto.getOriginUrl().startsWith("http://") && !dto.getOriginUrl().startsWith("https://")){
            throw new ServiceException("请输入http://或者https://开头的短链接");
        }
        // URL安全检查（黑名单/内容合规）
        urlSecurityCheckHandler.checkUrl(dto.getOriginUrl());
        // 0. gid不变直接更新
        if(StrUtil.isNotEmpty(dto.getOriginGid()) && dto.getOriginGid().equals(dto.getGid())){
            Link oldLink = lambdaQuery()
                    .eq(Link::getGid, dto.getOriginGid())
                    .eq(Link::getFullShortUrl, dto.getFullShortUrl())
                    .eq(Link::getEnableStatus, 0)
                    .one();
            if (oldLink == null) {
                throw new ServiceException("短链接不存在");
            }
            boolean updated = lambdaUpdate().eq(Link::getGid, dto.getOriginGid())
                    .eq(Link::getFullShortUrl, dto.getFullShortUrl())
                    .set(StrUtil.isNotEmpty(dto.getOriginUrl()), Link::getOriginUrl, dto.getOriginUrl())
                    .set(StrUtil.isNotEmpty(dto.getDescribe()), Link::getDescribe, dto.getDescribe())
                    .set(dto.getCreatedType() != null, Link::getCreatedType, dto.getCreatedType())
                    .set(dto.getValidDateType() != null, Link::getValidDateType, dto.getValidDateType())
                    .set(dto.getValidDate() != null, Link::getValidDate, dto.getValidDate())
                    .set(Link::getUpdateTime, LocalDateTime.now())
                    .update();
            if(!updated){
                throw new ServiceException("短链接更新失败");
            }
            deleteGroupCountCache(dto.getOriginGid());
            if (!oldLink.getOriginUrl().equals(dto.getOriginUrl())) {
                sendAuxiliaryMessagesAfterCommit(dto.getFullShortUrl(), dto.getOriginUrl(), dto.getGid());
            }
            return true;
        }

        // 1. 查询原始短链接
        Link link = lambdaQuery()
                .eq(Link::getGid, dto.getOriginGid())
                .eq(Link::getFullShortUrl, dto.getFullShortUrl())
                .eq(Link::getEnableStatus, 0)
                .one();
        if (link == null) {
            throw new ServiceException("短链接不存在");
        }
        String oldOriginUrl = link.getOriginUrl();

        // 2. 分组+原始链接唯一校验
        boolean needCheck = !link.getGid().equals(dto.getGid()) || !link.getOriginUrl().equals(dto.getOriginUrl());
        if (needCheck) {
            Long count = lambdaQuery()
                    .eq(Link::getGid, dto.getGid())
                    .eq(Link::getOriginUrl, dto.getOriginUrl())
                    .ne(Link::getId, link.getId())
                    .count();
            if (count > 0) {
                throw new ServiceException("该分组下已存在此原始链接，请勿重复创建！");
            }
        }

        // 3. 赋值新字段
        link.setGid(dto.getGid());
        link.setOriginUrl(dto.getOriginUrl());
        link.setDescribe(dto.getDescribe());
        link.setCreatedType(dto.getCreatedType() != null ? dto.getCreatedType() : link.getCreatedType());
        link.setValidDateType(dto.getValidDateType() != null ? dto.getValidDateType() : link.getValidDateType());
        link.setValidDate(dto.getValidDate() != null ? dto.getValidDate() : link.getValidDate());
        link.setUpdateTime(LocalDateTime.now());

        // 4. 删除旧数据：ID 必须传 link.getId()，不是 gid！
        boolean removed_1 = remove(new LambdaQueryWrapper<Link>()
                .eq(Link::getGid, dto.getOriginGid())
                .eq(Link::getFullShortUrl, dto.getFullShortUrl()));
        // 删除go_to 表中的旧数据
        boolean removed_2 = linkGotoService.remove(new LambdaQueryWrapper<LinkGoto>().eq(LinkGoto::getFullShortUrl, dto.getFullShortUrl())
                .eq(LinkGoto::getGid, dto.getOriginGid()));
        if (!removed_1 || !removed_2) {
            throw new ServiceException("短链接更新失败");
        }

        // 5. 插入新数据
        link.setId(null);
        boolean saved_1 = save(link);
        if (!saved_1) {
            throw new ServiceException("短链接更新失败");
        }
        saveLinkGoto(link.getFullShortUrl(), link.getGid());
        deleteGroupCountCache(dto.getOriginGid(), link.getGid());
        // 6. 如果原始链接变化，异步通过MQ获取新favicon
        if (!oldOriginUrl.equals(dto.getOriginUrl())) {
            sendAuxiliaryMessagesAfterCommit(link.getFullShortUrl(), dto.getOriginUrl(), link.getGid());
        }

        return true;
    }

    @Override
    public PageResult<LinkPageResponseDto> pageLink(LinkPageQueryDto pageQuery) {
        long current = pageQuery.getCurrent() != null ? pageQuery.getCurrent() : 1L;
        long size = pageQuery.getSize() != null ? pageQuery.getSize() : 10L;
        Page<Link> page = new Page<>(current, size);
        boolean asc = isAscOrder(pageQuery.getOrderType());

        // 按统计字段排序时走单独分支：统计值不在主表，库里排不了，
        // 必须先取本分组全量再排序分页，否则只会排当前页（跨页顺序错乱）
        if (isStatsOrderTag(pageQuery.getOrderTag())) {
            return pageLinkOrderByStats(pageQuery.getGid(), current, size, asc, pageQuery.getOrderTag());
        }

        // 1. 构建基础查询条件
        var queryWrapper = Wrappers.lambdaQuery(Link.class)
                .eq(Link::getGid, pageQuery.getGid())
                .eq(Link::getEnableStatus, 0);

        // 2. 处理数据库字段排序（仅支持 createTime / updateTime，修复Lambda排序错误）
        String orderTag = pageQuery.getOrderTag();
        if (StrUtil.isNotEmpty(orderTag)) {
            switch (orderTag) {
                case "createTime":
                    queryWrapper.orderBy(true, asc, Link::getCreateTime);
                    break;
                case "updateTime":
                    queryWrapper.orderBy(true, asc, Link::getUpdateTime);
                    break;
                default:
                    queryWrapper.orderBy(true, asc, Link::getUpdateTime);
            }
        } else {
            queryWrapper.orderByDesc(Link::getUpdateTime);
        }

        // 3. 查询短链接主表
        Page<Link> linkPage = baseMapper.selectPage(page, queryWrapper);
        List<LinkPageResponseDto> records = BeanUtil.copyToList(linkPage.getRecords(), LinkPageResponseDto.class);

        // 4. 批量填充 PV/UV/UIP 统计数据（两条统计查询并行执行）
        fillStats(records, pageQuery.getGid());

        // 5. 封装分页结果
        return PageResult.<LinkPageResponseDto>builder()
                .size(linkPage.getSize())
                .current(linkPage.getCurrent())
                .pages(linkPage.getPages())
                .total(linkPage.getTotal())
                .records(records)
                .build();
    }

    /**
     * 是否是按统计字段排序
     */
    private boolean isStatsOrderTag(String orderTag) {
        if (StrUtil.isEmpty(orderTag)) {
            return false;
        }
        return switch (orderTag) {
            case "todayPv", "totalPv", "todayUv", "totalUv", "todayUip", "totalUip" -> true;
            default -> false;
        };
    }

    /**
     * 按统计字段排序分页。
     * 统计值在 t_link_access_stats，无法和主表在库里一起排序，因此取本分组全量后内存排序再切片；
     * 这样跨页顺序才是全局正确的（原实现只对当前页排序）。分组链接数很大时是一次全量查询，属该排序方式固有代价。
     */
    private PageResult<LinkPageResponseDto> pageLinkOrderByStats(String gid, long current, long size,
                                                                boolean asc, String orderTag) {
        List<Link> allLinks = this.list(Wrappers.lambdaQuery(Link.class)
                .eq(Link::getGid, gid)
                .eq(Link::getEnableStatus, 0));
        long total = allLinks.size();
        if (total == 0) {
            return PageResult.<LinkPageResponseDto>builder()
                    .size(size)
                    .current(current)
                    .pages(0L)
                    .total(0L)
                    .records(Collections.emptyList())
                    .build();
        }

        List<LinkPageResponseDto> allRecords = BeanUtil.copyToList(allLinks, LinkPageResponseDto.class);
        fillStats(allRecords, gid);

        Comparator<LinkPageResponseDto> comparator = buildStatsComparator(orderTag);
        if (comparator == null) {
            comparator = Comparator.comparing(LinkPageResponseDto::getUpdateTime,
                    Comparator.nullsLast(Comparator.naturalOrder()));
        }
        // 统计值相同的记录按创建时间排序，保证分页顺序稳定（否则同一条件下多次查询顺序可能不同）
        Comparator<LinkPageResponseDto> tieBreaker = Comparator.comparing(
                LinkPageResponseDto::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder()));
        allRecords.sort(asc
                ? comparator.thenComparing(tieBreaker)
                : comparator.reversed().thenComparing(tieBreaker.reversed()));

        // 用 long 计算偏移，避免 current 很大时整型溢出导致 subList 下标为负
        long fromL = Math.max(0L, Math.min((current - 1) * size, total));
        int from = (int) fromL;
        int to = (int) Math.min(fromL + size, total);
        List<LinkPageResponseDto> pageRecords = new ArrayList<>(allRecords.subList(from, to));

        long pages = (total + size - 1) / size;
        return PageResult.<LinkPageResponseDto>builder()
                .size(size)
                .current(current)
                .pages(pages)
                .total(total)
                .records(pageRecords)
                .build();
    }

    private Comparator<LinkPageResponseDto> buildStatsComparator(String orderTag) {
        if (orderTag == null) {
            return null;
        }
        return switch (orderTag) {
            case "todayPv" -> Comparator.comparingInt(r -> safeInt(r.getTodayPv()));
            case "totalPv" -> Comparator.comparingInt(r -> safeInt(r.getTotalPv()));
            case "todayUv" -> Comparator.comparingInt(r -> safeInt(r.getTodayUv()));
            case "totalUv" -> Comparator.comparingInt(r -> safeInt(r.getTotalUv()));
            case "todayUip" -> Comparator.comparingInt(r -> safeInt(r.getTodayUip()));
            case "totalUip" -> Comparator.comparingInt(r -> safeInt(r.getTotalUip()));
            default -> null;
        };
    }

    private static int safeInt(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 批量填充 PV/UV/UIP。
     * 两条统计查询互不依赖，并行执行以省掉一次数据库往返；
     * 任一失败只降级为 0，不影响列表本身返回。
     */
    private void fillStats(List<LinkPageResponseDto> records, String gid) {
        if (CollUtil.isEmpty(records)) {
            return;
        }
        List<String> fullShortUrls = records.stream()
                .map(LinkPageResponseDto::getFullShortUrl)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (fullShortUrls.isEmpty()) {
            return;
        }

        // 数据库已迁到本地，单次查询 1ms 量级，串行执行即可，不需要线程池
        List<Map<String, Object>> statsList;
        try {
            statsList = linkAccessStatsMapper.selectPagePvUvUipStats(fullShortUrls, gid);
        } catch (Exception e) {
            log.warn("[列表统计] PV/UV/UIP 查询失败，降级为 0", e);
            statsList = Collections.emptyList();
        }

        Map<String, Map<String, Integer>> visitorMap;
        try {
            visitorMap = linkAccessLogStatsService.getPageDistinctVisitor(fullShortUrls);
        } catch (Exception e) {
            log.warn("[列表统计] 去重访客查询失败，降级为 0", e);
            visitorMap = Collections.emptyMap();
        }

        Map<String, Map<String, Object>> statsMap = statsList.stream()
                .filter(item -> item.get("full_short_url") != null)
                .collect(Collectors.toMap(
                        item -> (String) item.get("full_short_url"),
                        item -> item,
                        (a, b) -> a
                ));

        for (LinkPageResponseDto record : records) {
            Map<String, Object> stat = statsMap.get(record.getFullShortUrl());
            record.setTotalPv(toInt(stat, "total_pv"));
            record.setTodayPv(toInt(stat, "today_pv"));
            // 今日 UV/UIP 取小时表即可（一天内按天去重，口径正确）
            record.setTodayUv(toInt(stat, "today_uv"));
            record.setTodayUip(toInt(stat, "today_uip"));
            // 累计 UV/UIP 用整个区间的去重结果
            Map<String, Integer> visitor = visitorMap.get(record.getFullShortUrl());
            if (visitor != null) {
                record.setTotalUv(safeInt(visitor.get("uv")));
                record.setTotalUip(safeInt(visitor.get("uip")));
            } else {
                record.setTotalUv(toInt(stat, "total_uv"));
                record.setTotalUip(toInt(stat, "total_uip"));
            }
        }
    }


    /**
     * 安全获取统计数字
     */
    private int toInt(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return 0;
        }
        return Integer.parseInt(map.get(key).toString());
    }

    private boolean isAscOrder(String orderType) {
        return StrUtil.equalsIgnoreCase(orderType, "asc");
    }


    @Override
    public List<LinkGroupCountResponseDto> listGroupShortLinkCount(List<String> requestParam) {
        if (CollUtil.isEmpty(requestParam)) {
            return new ArrayList<>();
        }

        List<String> gidList = requestParam.stream()
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
        if (CollUtil.isEmpty(gidList)) {
            return new ArrayList<>();
        }

        Map<String, Integer> countMap = new HashMap<>(gidList.size());
        List<String> missGidList = new ArrayList<>();
        List<String> cacheKeys = gidList.stream()
                .map(gid -> String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid))
                .toList();
        List<String> cacheValues = redisTemplate.opsForValue().multiGet(cacheKeys);
        for (int i = 0; i < gidList.size(); i++) {
            String gid = gidList.get(i);
            String cacheValue = cacheValues == null ? null : cacheValues.get(i);
            Integer count = parseCachedCount(cacheValue);
            if (count == null) {
                missGidList.add(gid);
            } else {
                countMap.put(gid, count);
            }
        }

        if (CollUtil.isNotEmpty(missGidList)) {
            // 3. 关键：使用 selectMaps() 获取统计结果
            List<Map<String, Object>> mapList = baseMapper.selectMaps(new QueryWrapper<Link>()
                    .select("gid", "COUNT(id) AS shortLinkCount")
                    .eq("enable_status", 0)
                    .eq("del_flag", 0)
                    .in("gid", missGidList)
                    .groupBy("gid"));

            // 4. 将Map结果转为 gid -> 数量 的Map
            Map<String, Integer> dbCountMap = mapList.stream()
                    .collect(Collectors.toMap(
                            map -> String.valueOf(map.get("gid")),
                            map -> Integer.valueOf(String.valueOf(map.get("shortLinkCount")))
                    ));
            for (String gid : missGidList) {
                Integer count = dbCountMap.getOrDefault(gid, 0);
                countMap.put(gid, count);
                redisTemplate.opsForValue().set(
                        String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid),
                        String.valueOf(count),
                        RedisConstant.GROUP_SHORT_LINK_COUNT_EXPIRE_SECONDS,
                        TimeUnit.SECONDS
                );
            }
        }

        // 5. 遍历所有传入的gid，补全0值
        return requestParam.stream()
                .map(gid -> new LinkGroupCountResponseDto(gid, countMap.getOrDefault(gid, 0)))
                .collect(Collectors.toList());
    }



    @Override
    public void restoreUrl(String shortUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String scheme = "https";
        String serverName = request.getServerName();
        String fullShortUrl = scheme + "://" + serverName + "/" + shortUri;
        String redisKey = String.format(RedisConstant.FULL_SHORT_URL_KEY, fullShortUrl);

        // 0. 布隆过滤器：不存在直接返回
        if (!shortLinkBloomFilter.contains(fullShortUrl)) {
            response.sendRedirect("/page/notfound");
            return;
        }

        // 1. 查缓存
        String originUrl = redisTemplate.opsForValue().get(redisKey);
        if (StrUtil.isNotEmpty(originUrl)) {
            // 缓存空值：无效链接
            if ("null".equals(originUrl)) {
                response.sendRedirect("/page/notfound");
                return;
            }
            // 统计 + 点击数 + 重定向
            handleRedirectAndStats(fullShortUrl, originUrl, request, response);
            return;
        }

        // 2. 缓存不存在，加锁查询数据库
        RLock lock = redissonClient.getLock(String.format(RedisConstant.LOCK_FULL_SHORT_URL_KEY, fullShortUrl));
        lock.lock();
        try {
            // 3. 双重检查缓存
            originUrl = redisTemplate.opsForValue().get(redisKey);
            if (StrUtil.isNotEmpty(originUrl)) {
                if ("null".equals(originUrl)) {
                    response.sendRedirect("/page/notfound");
                    return;
                }
                handleRedirectAndStats(fullShortUrl, originUrl, request, response);
                return;
            }

            // 4. 查询数据库
            LinkGoto linkGoto = linkGotoService.getOne(new LambdaQueryWrapper<LinkGoto>().eq(LinkGoto::getFullShortUrl, fullShortUrl));
            if (linkGoto == null) {
                // 缓存空值防穿透
                redisTemplate.opsForValue().set(redisKey, "null", SHORT_LINK_GOTO_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }

            // 查询有效短链接
            Link link = getOne(new LambdaQueryWrapper<Link>()
                    .eq(Link::getGid, linkGoto.getGid())
                    .eq(Link::getFullShortUrl, fullShortUrl)
                    .eq(Link::getEnableStatus, 0)
                    .ge(Link::getValidDate, LocalDateTime.now()));

            if (link == null) {
                redisTemplate.opsForValue().set(redisKey, "null", SHORT_LINK_GOTO_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                response.sendRedirect("/page/notfound");
                return;
            }

            // 5. 查询成功，设置缓存
            originUrl = link.getOriginUrl();
            redisTemplate.opsForValue().set(redisKey, originUrl, SHORT_LINK_GOTO_CACHE_TTL_SECONDS, TimeUnit.SECONDS);

            // 统计 + 点击数 + 重定向
            handleRedirectAndStats(fullShortUrl, originUrl, request, response);

        } finally {
            lock.unlock();
        }
    }

    /**
     * 执行顺序：统计 → 点击数 → 重定向（保证全部执行）
     */
    private void handleRedirectAndStats(String fullShortUrl, String originUrl,
                                        HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. 先更新访问统计（PV/UV/UIP）
        linkAccessStatsService.updateLinkAccessStats(fullShortUrl, request, response);
        // 2. 立即重定向，不阻塞
        response.sendRedirect(originUrl);
        // 3. 再更新点击数——轻量级SQL，保留同步
        addClick(fullShortUrl);
        // 4. 异步通过MQ处理全维度统计（地区/OS/浏览器/设备/网络/访问日志）
        // 获取客户端IP
        String clientIp = ipUtils.getClientIp(request);
        // 获取操作系统
        String osName = OsUtils.getOsName(request);
        // 获取浏览器
        String browser = BrowserUtils.getBrowserName(request);
        // 获取设备
        String device = DeviceUtils.getDeviceType(request);
        // 获取网络类型
        String networkType = NetworkUtils.getNetworkType(request);

        rabbitMQProducer.sendStatsMessage(StatsMessage.builder()
                .fullShortUrl(fullShortUrl)
                .ip(clientIp)
                .osName(osName)
                .browserName(browser)
                .deviceName(device)
                .networkType(networkType)
                .build());

    }


    @Override
    public void addClick(String fullShortUrl) {

        // 1. 获取 分片键gid
        LinkGoto linkGoto = linkGotoService
                .getOne(new LambdaQueryWrapper<LinkGoto>().eq(LinkGoto::getFullShortUrl, fullShortUrl));
        if (linkGoto == null) {
            throw new ServiceException("短链接不存在");
        }
        String gid = linkGoto.getGid();
        // 2. 短链接点击数+1
        boolean updated = lambdaUpdate().setSql("click_num = click_num + 1")
                .eq(Link::getGid, gid)
                .eq(Link::getFullShortUrl, fullShortUrl).update();
        if (!updated) {
            throw new ServiceException("短链接点击数更新失败");
        }

    }

    private void saveLinkGoto(String fullShortUrl, String gid) {
        boolean saved = linkGotoService.save(LinkGoto.builder()
                .fullShortUrl(fullShortUrl)
                .gid(gid)
                .build());
        if (!saved) {
            throw new ServiceException("短链接跳转关系写入失败");
        }
    }

    private void sendAuxiliaryMessagesAfterCommit(String fullShortUrl, String originUrl, String gid) {
        Runnable task = () -> {
            rabbitMQProducer.sendFaviconMessage(FaviconMessage.builder()
                    .fullShortUrl(fullShortUrl)
                    .originUrl(originUrl)
                    .gid(gid)
                    .build());
            rabbitMQProducer.sendUrlSecurityMessage(UrlSecurityMessage.builder()
                    .fullShortUrl(fullShortUrl)
                    .originUrl(originUrl)
                    .gid(gid)
                    .build());
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    task.run();
                }
            });
            return;
        }
        task.run();
    }

    @Override
    public String getTitleByUrl(String url) {
        String targetUrl = normalizeTitleUrl(url);
        if (!StringUtils.hasText(targetUrl)) {
            return "";
        }
        String titleCacheKey = TITLE_CACHE_PREFIX + SecureUtil.md5(targetUrl);
        String cachedTitle = redisTemplate.opsForValue().get(titleCacheKey);
        if (StringUtils.hasText(cachedTitle)) {
            if (UNREACHABLE_TITLE_CACHE_VALUE.equals(cachedTitle)) {
                return "";
            }
            return EMPTY_TITLE_CACHE_VALUE.equals(cachedTitle) ? "" : cachedTitle;
        }
        TitleProbeResult probeResult = probeTitleUrl(targetUrl);
        if (TitleProbeResult.NOT_EXISTS.equals(probeResult)) {
            throw new ServiceException("目标网站不可访问，请检查链接是否正确");
        }
        try {
            Document document = Jsoup.connect(targetUrl)
                    .timeout(TITLE_FETCH_TIMEOUT_MS)
                    .maxBodySize(TITLE_MAX_BODY_SIZE)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .referrer("https://www.google.com")
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .followRedirects(true)
                    .get();
            String title = StrUtil.trimToEmpty(document.title());
            redisTemplate.opsForValue().set(
                    titleCacheKey,
                    StringUtils.hasText(title) ? title : EMPTY_TITLE_CACHE_VALUE,
                    TITLE_CACHE_TTL_SECONDS,
                    TimeUnit.SECONDS
            );
            return title;
        } catch (IOException e) {
            log.warn("获取URL标题失败, url={}", targetUrl, e);
            redisTemplate.opsForValue().set(titleCacheKey, EMPTY_TITLE_CACHE_VALUE, TITLE_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
            return "";
        }
    }

    private String normalizeTitleUrl(String url) {
        if (!StringUtils.hasText(url)) {
            throw new ServiceException("链接不能为空");
        }
        try {
            URL targetUrl = new URL(url);
            String protocol = targetUrl.getProtocol();
            if ((!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol))
                    || !StringUtils.hasText(targetUrl.getHost())) {
                throw new ServiceException("链接格式不合法，请输入正确的 http:// 或 https:// 链接");
            }
            return targetUrl.toString();
        } catch (Exception e) {
            if (e instanceof ServiceException serviceException) {
                throw serviceException;
            }
            log.warn("URL格式不合法, url={}", url);
            throw new ServiceException("链接格式不合法，请输入正确的 http:// 或 https:// 链接");
        }
    }

    private TitleProbeResult probeTitleUrl(String url) {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(TITLE_PROBE_TIMEOUT_MS);
            connection.setReadTimeout(TITLE_PROBE_TIMEOUT_MS);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            int status = connection.getResponseCode();
            if ((status >= HttpURLConnection.HTTP_OK && status < HttpURLConnection.HTTP_BAD_REQUEST)
                    || status == HttpURLConnection.HTTP_FORBIDDEN
                    || status == HttpURLConnection.HTTP_UNAUTHORIZED
                    || status == HttpURLConnection.HTTP_BAD_METHOD) {
                return TitleProbeResult.EXISTS;
            }
            log.warn("URL标题探针状态不确定, url={}, status={}", url, status);
            return TitleProbeResult.UNKNOWN;
        } catch (UnknownHostException e) {
            log.warn("URL标题探针域名解析失败, url={}, message={}", url, e.getMessage());
            return TitleProbeResult.NOT_EXISTS;
        } catch (SocketTimeoutException e) {
            log.warn("URL标题探针请求超时, url={}, message={}", url, e.getMessage());
            return TitleProbeResult.UNKNOWN;
        } catch (IOException e) {
            log.warn("URL标题探针请求失败, url={}, message={}", url, e.getMessage());
            return TitleProbeResult.UNKNOWN;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private enum TitleProbeResult {
        EXISTS,
        NOT_EXISTS,
        UNKNOWN
    }


    public String getFaviconUrl(String url) {
        if (url == null || url.isBlank()) {
            return DEFAULT_FAVICON_URL;
        }
        String cacheKey = null;
        try {
            String siteUrl = getSiteRootUrl(url);
            cacheKey = FAVICON_CACHE_PREFIX + SecureUtil.md5(siteUrl);
            String cachedFavicon = redisTemplate.opsForValue().get(cacheKey);
            if (StringUtils.hasText(cachedFavicon)) {
                return cachedFavicon;
            }
            URL targetUrl = new URL(siteUrl);
            List<String> faviconCandidates = new ArrayList<>();
            collectFaviconCandidatesFromHtml(siteUrl, faviconCandidates);
            addFaviconCandidate(faviconCandidates, targetUrl.getProtocol() + "://" + targetUrl.getHost() + "/favicon.ico");
            addFaviconCandidate(faviconCandidates, targetUrl.getProtocol() + "://" + targetUrl.getHost() + "/apple-touch-icon.png");
            for (String faviconUrl : faviconCandidates) {
                if (isValidFaviconUrl(faviconUrl)) {
                    redisTemplate.opsForValue().set(cacheKey, faviconUrl, FAVICON_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
                    return faviconUrl;
                }
            }
        } catch (Exception e) {
            log.warn("解析获取favicon失败,返回默认图标: {}", url, e);
        }
        if (StringUtils.hasText(cacheKey)) {
            redisTemplate.opsForValue().set(cacheKey, DEFAULT_FAVICON_URL, FAVICON_CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        }
        return DEFAULT_FAVICON_URL;
    }

    private long elapsedMillis(long startTime) {
        return System.currentTimeMillis() - startTime;
    }

    private String getSiteRootUrl(String url) throws IOException {
        URL targetUrl = new URL(url);
        int port = targetUrl.getPort();
        String portPart = port > 0 ? ":" + port : "";
        return targetUrl.getProtocol() + "://" + targetUrl.getHost() + portPart;
    }

    private void collectFaviconCandidatesFromHtml(String url, List<String> faviconCandidates) {
        try {
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(FAVICON_FETCH_TIMEOUT_MS)
                    .maxBodySize(FAVICON_MAX_BODY_SIZE)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .get();
            document.select("link[rel~=(?i)(shortcut\\s+icon|icon|apple-touch-icon|mask-icon)]")
                    .forEach(element -> {
                        String faviconUrl = element.attr("abs:href");
                        addFaviconCandidate(faviconCandidates, faviconUrl);
                    });
        } catch (Exception e) {
            log.warn("从HTML解析favicon候选失败, url={}, message={}", url, e.getMessage());
        }
    }

    private void addFaviconCandidate(List<String> faviconCandidates, String faviconUrl) {
        if (StringUtils.hasText(faviconUrl) && !faviconCandidates.contains(faviconUrl)) {
            faviconCandidates.add(faviconUrl);
        }
    }

    private boolean isValidFaviconUrl(String faviconUrl) {
        if (!StringUtils.hasText(faviconUrl)) {
            return false;
        }
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(faviconUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(FAVICON_CHECK_TIMEOUT_MS);
            connection.setReadTimeout(FAVICON_CHECK_TIMEOUT_MS);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            int status = connection.getResponseCode();
            String contentType = connection.getContentType();
            return status >= HttpURLConnection.HTTP_OK
                    && status < HttpURLConnection.HTTP_BAD_REQUEST
                    && StringUtils.hasText(contentType)
                    && (contentType.startsWith("image/") || contentType.contains("icon"));
        } catch (Exception e) {
            log.debug("favicon候选不可用, url={}, message={}", faviconUrl, e.getMessage());
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }




    /**
     * 批量查询：该分组下已存在的原始链接集合
     */
    private Set<String> getExistOriginUrlSet(String gid, List<LinkCreateRequestDto> dtoList) {
        List<String> originUrlList = dtoList.stream()
                .map(LinkCreateRequestDto::getOriginUrl)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getGid, gid)
                .in(Link::getOriginUrl, originUrlList)
                .select(Link::getOriginUrl);

        List<Link> existLinks = list(queryWrapper);
        return existLinks.stream()
                .map(Link::getOriginUrl)
                .collect(Collectors.toSet());
    }

    /**
     * 批量构建 Link 实体对象
     */
    private List<Link> buildBatchLinkList(List<LinkCreateRequestDto> needCreateList) {
        List<Link> linkList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (LinkCreateRequestDto dto : needCreateList) {
            try {
                String domain = "https://" + dto.getDomain();
                // 生成短链接
                String shortUri = shortUrlGeneratorUtils.generateShortCode(domain, dto.getOriginUrl());
                String fullShortUrl = domain + "/" + shortUri;

                Link link = Link.builder()
                        .domain(domain)
                        .shortUri(shortUri)
                        .fullShortUrl(fullShortUrl)
                        .originUrl(dto.getOriginUrl())
                        .gid(dto.getGid())
                        .clickNum(0)
                        .enableStatus(0)
                        .createdType(Objects.requireNonNullElse(dto.getCreatedType(), 0))
                        .validDateType(Objects.requireNonNullElse(dto.getValidDateType(), 0))
                        .describe(dto.getDescribe())
                        // favicon通过MQ异步获取，初始为null
                        .favicon(null)
                        .build();

                // 设置有效期
                if (Objects.equals(dto.getValidDateType(), 1) && dto.getValidDate() != null) {
                    link.setValidDate(dto.getValidDate());
                } else {
                    link.setValidDate(now.plusDays(30));
                }

                linkList.add(link);
            } catch (Exception e) {
                log.error("构建短链接实体失败，originUrl:{}", dto.getOriginUrl(), e);
            }
        }
        return linkList;
    }

    private void deleteGroupCountCache(String... gids) {
        if (gids == null || gids.length == 0) {
            return;
        }
        List<String> keys = Arrays.stream(gids)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .map(gid -> String.format(RedisConstant.GROUP_SHORT_LINK_COUNT_KEY, gid))
                .toList();
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    redisTemplate.delete(keys);
                }
            });
        } else {
            redisTemplate.delete(keys);
        }
    }

    private Integer parseCachedCount(String cacheValue) {
        if (StrUtil.isBlank(cacheValue)) {
            return null;
        }
        try {
            return Integer.parseInt(cacheValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

}
