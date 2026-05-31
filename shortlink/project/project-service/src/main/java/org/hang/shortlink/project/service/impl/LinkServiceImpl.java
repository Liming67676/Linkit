package org.hang.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
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
import org.hang.shortlink.project.mq.dto.LinkGotoMessage;
import org.hang.shortlink.project.mq.dto.StatsMessage;
import org.hang.shortlink.project.mq.producer.RabbitMQProducer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
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

    private final ShortUrlGeneratorUtils shortUrlGeneratorUtils;

    private final RedissonClient redissonClient;

    private final ILinkGotoService linkGotoService;

    private final StringRedisTemplate redisTemplate;

    private final RBloomFilter<String> shortLinkBloomFilter;

    private final ILinkAccessStatsService linkAccessStatsService;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    private final IpUtils ipUtils; // IP工具类



    private final UrlSecurityCheckHandler urlSecurityCheckHandler;

    private final RabbitMQProducer rabbitMQProducer;

    @Transactional
    @Override
    public LikCreateResponseDto createLink(LinkCreateRequestDto dto) {
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
        urlSecurityCheckHandler.checkUrl(dto.getOriginUrl());

        // 同一个分组只能有唯一一个源地址
        String lockKey = "link:create:" + dto.getGid() + ":" + dto.getOriginUrl();
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(); // 阻塞锁
        try {
            Long count = lambdaQuery().eq(Link::getGid, dto.getGid())
                    .eq(Link::getOriginUrl, dto.getOriginUrl())
                    .count();
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
                // todo 定时任务处理过期
            }
            boolean saved = save(link);
            if (!saved) {
                throw new ServiceException("新增当个短链接失败");
            }
            // 异步发送 MQ 消息：获取 favicon 并更新
            rabbitMQProducer.sendFaviconMessage(FaviconMessage.builder()
                    .fullShortUrl(link.getFullShortUrl())
                    .originUrl(link.getOriginUrl())
                    .gid(link.getGid())
                    .build());
            // 异步发送 MQ 消息：写入 LinkGoto
            rabbitMQProducer.sendGotoMessage(LinkGotoMessage.builder()
                    .fullShortUrl(link.getFullShortUrl())
                    .gid(link.getGid())
                    .build());
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
        for (int i = 0; i < originUrls.size(); i++) {
            // 排除不是http:// 或https:// 开头的短链接
            if(!originUrls.get(i).startsWith("http://") && !originUrls.get(i).startsWith("https://")){
                throw new ServiceException("请输入http://或者https://开头的短链接");
            }
            // URL安全检查（黑名单/内容合规）
            urlSecurityCheckHandler.checkUrl(originUrls.get(i));
            String originUrl = originUrls.get(i);
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

            // 9. 异步通过MQ批量写入LinkGoto + 获取favicon
            for (Link link : linkList) {
                rabbitMQProducer.sendFaviconMessage(FaviconMessage.builder()
                        .fullShortUrl(link.getFullShortUrl())
                        .originUrl(link.getOriginUrl())
                        .gid(link.getGid())
                        .build());
                rabbitMQProducer.sendGotoMessage(LinkGotoMessage.builder()
                        .fullShortUrl(link.getFullShortUrl())
                        .gid(link.getGid())
                        .build());
            }

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
        // 异步通过MQ写入新LinkGoto
        rabbitMQProducer.sendGotoMessage(LinkGotoMessage.builder()
                .fullShortUrl(link.getFullShortUrl())
                .gid(link.getGid())
                .build());
        if (!saved_1) {
            throw new ServiceException("短链接更新失败");
        }
        // 6. 如果原始链接变化，异步通过MQ获取新favicon
        if (!link.getOriginUrl().equals(dto.getOriginUrl())) {
            rabbitMQProducer.sendFaviconMessage(FaviconMessage.builder()
                    .fullShortUrl(link.getFullShortUrl())
                    .originUrl(dto.getOriginUrl())
                    .gid(link.getGid())
                    .build());
        }

        return true;
    }

    @Override
    public PageResult<LinkPageResponseDto> pageLink(LinkPageQueryDto pageQuery) {
        long current = pageQuery.getCurrent() != null ? pageQuery.getCurrent() : 1L;
        long size = pageQuery.getSize() != null ? pageQuery.getSize() : 10L;
        Page<Link> page = new Page<>(current, size);

        // 1. 构建基础查询条件
        var queryWrapper = Wrappers.lambdaQuery(Link.class)
                .eq(Link::getGid, pageQuery.getGid())
                .eq(Link::getEnableStatus, 0);

        // 2. 处理数据库字段排序（仅支持 createTime / updateTime，修复Lambda排序错误）
        String orderTag = pageQuery.getOrderTag();
        if (StrUtil.isNotEmpty(orderTag)) {
            switch (orderTag) {
                case "createTime":
                    queryWrapper.orderByDesc(Link::getCreateTime);
                    break;
                case "updateTime":
                    queryWrapper.orderByDesc(Link::getUpdateTime);
                    break;
                default:
                    // 默认排序：更新时间倒序
                    queryWrapper.orderByDesc(Link::getUpdateTime);
            }
        } else {
            queryWrapper.orderByDesc(Link::getUpdateTime);
        }

        // 3. 查询短链接主表
        Page<Link> linkPage = baseMapper.selectPage(page, queryWrapper);
        List<LinkPageResponseDto> records = BeanUtil.copyToList(linkPage.getRecords(), LinkPageResponseDto.class);

        // 4. 批量填充 PV/UV/UIP 统计数据
        if (!records.isEmpty()) {
            List<String> fullShortUrls = records.stream()
                    .map(LinkPageResponseDto::getFullShortUrl)
                    .filter(Objects::nonNull)
                    .toList();
            if (!fullShortUrls.isEmpty()) {
                List<Map<String, Object>> statsList = linkAccessStatsMapper.selectPagePvUvUipStats(fullShortUrls, pageQuery.getGid());
                Map<String, Map<String, Object>> statsMap = statsList.stream()
                        .collect(Collectors.toMap(
                                s -> (String) s.get("full_short_url"),
                                s -> s
                        ));
                for (LinkPageResponseDto record : records) {
                    Map<String, Object> stat = statsMap.get(record.getFullShortUrl());
                    record.setTotalPv(toInt(stat, "total_pv"));
                    record.setTotalUv(toInt(stat, "total_uv"));
                    record.setTotalUip(toInt(stat, "total_uip"));
                    record.setTodayPv(toInt(stat, "today_pv"));
                    record.setTodayUv(toInt(stat, "today_uv"));
                    record.setTodayUip(toInt(stat, "today_uip"));
                }
            }
        }

        // 5. 统计字段内存排序（todayPv / totalPv 等）
        if (StrUtil.isNotEmpty(orderTag)) {
            switch (orderTag) {
                case "todayPv":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTodayPv).reversed());
                    break;
                case "totalPv":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTotalPv).reversed());
                    break;
                case "todayUv":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTodayUv).reversed());
                    break;
                case "totalUv":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTotalUv).reversed());
                    break;
                case "todayUip":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTodayUip).reversed());
                    break;
                case "totalUip":
                    records.sort(Comparator.comparingInt(LinkPageResponseDto::getTotalUip).reversed());
                    break;
            }
        }

        // 6. 封装分页结果
        return PageResult.<LinkPageResponseDto>builder()
                .size(linkPage.getSize())
                .current(linkPage.getCurrent())
                .pages(linkPage.getPages())
                .total(linkPage.getTotal())
                .records(records)
                .build();
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


    @Override
    public List<LinkGroupCountResponseDto> listGroupShortLinkCount(List<String> requestParam) {
        if (CollUtil.isEmpty(requestParam)) {
            return new ArrayList<>();
        }


        // 3. 关键：使用 selectMaps() 获取统计结果
        List<Map<String, Object>> mapList = baseMapper.selectMaps(new QueryWrapper<Link>()
                .select("gid", "COUNT(id) AS shortLinkCount")
                .eq("enable_status", 0)
                .in("gid", requestParam)
                .groupBy("gid"));

        // 4. 将Map结果转为 gid -> 数量 的Map
        Map<String, Integer> countMap = mapList.stream()
                .collect(Collectors.toMap(
                        map -> String.valueOf(map.get("gid")),
                        map -> Integer.valueOf(String.valueOf(map.get("shortLinkCount")))
                ));

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
                redisTemplate.opsForValue().set(redisKey, "null", 3, TimeUnit.MINUTES);
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
                redisTemplate.opsForValue().set(redisKey, "null", 3, TimeUnit.MINUTES);
                response.sendRedirect("/page/notfound");
                return;
            }

            // 5. 查询成功，设置缓存
            originUrl = link.getOriginUrl();
            redisTemplate.opsForValue().set(redisKey, originUrl, 30, TimeUnit.MINUTES);

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

    @Override
    public String getTitleByUrl(String url) {
        try {
            // 使用Jsoup直接连接URL并获取文档
            Document document = Jsoup.connect(url)
                    .timeout(10000) // 设置10秒超时
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .followRedirects(true) // 跟随重定向
                    .get();
            return document.title();
        } catch (IOException e) {
            System.err.println("获取URL标题时发生错误: " + e.getMessage());
            return "";
        }
    }


    public String getFaviconUrl(String url) {
        // 空值校验
        if (url == null || url.isBlank()) {
            return null;
        }

        try {
            // 1. 配置 Jsoup 请求（模拟浏览器，防拦截）
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .ignoreHttpErrors(true)
                    .followRedirects(true)
                    .get();

            // 2. 解析网页图标标签
            Element faviconLink = document.selectFirst("link[rel~=(?i)(shortcut\\s+)?icon]");
            if (faviconLink != null) {
                String faviconUrl = faviconLink.attr("abs:href");
                if (faviconUrl != null && !faviconUrl.isBlank()) {
                    return faviconUrl;
                }
            }

            // 3. 兜底方案：网站默认根目录图标（99%网站通用）
            URL targetUrl = new URL(url);
            return targetUrl.getProtocol() + "://" + targetUrl.getHost() + "/favicon.ico";

        } catch (Exception e) {
            log.error("解析获取favicon失败,返回默认图标: {}", url, e);
            return "https://img.icons8.com/fluency/32/000000/link.png";
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

}
