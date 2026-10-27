package org.hang.shortlink.project.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.exception.ServiceException;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkGroupStatsRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsRequestDto;
import org.hang.shortlink.project.dto.stats.response.*;
import org.hang.shortlink.project.mapper.*;
import org.hang.shortlink.project.service.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements IShortLinkStatsService {

    private final LinkMapper linkMapper;
    private final ILinkAccessStatsService linkAccessStatsService;
    private final ILinkLocaleStatsService linkLocaleStatsService;
    private final ILinkBrowserStatsService linkBrowserStatsService;
    private final ILinkOsStatsService linkOsStatsService;
    private final ILinkDeviceStatsService linkDeviceStatsService;
    private final ILinkNetworkStatsService linkNetworkStatsService;
    private final ILinkAccessLogStatsService linkAccessLogStatsService;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsRequestDto statsRequest) {
        if(statsRequest == null){
            throw new ServiceException("参数为空");
        }
        String fullShortUrl = statsRequest.getFullShortUrl();
        LocalDateTime start = statsRequest.getStartDate();
        LocalDateTime end = statsRequest.getEndDate();
        // ====================== 初始化默认值 ======================
        // 1. PV/UV/UIP 默认值
        Map<String, Integer> totalMap = new HashMap<>();
        totalMap.put("pv", 0);
        totalMap.put("uv", 0);
        totalMap.put("uip", 0);
        // 2. 各类统计列表默认空集合
        List<ShortLinkStatsAccessDailyResponseDto> daily = Collections.emptyList();
        List<ShortLinkStatsLocaleCNResponseDto> localeCnStats = Collections.emptyList();
        List<Integer> hourStats = Collections.emptyList();
        List<ShortLinkStatsTopIpResponseDto> topIpStats = Collections.emptyList();
        List<Integer> weekdayStats = Collections.emptyList();
        List<ShortLinkStatsBrowserResponseDto> browserStats = Collections.emptyList();
        List<ShortLinkStatsOsResponseDto> osStats = Collections.emptyList();
        List<ShortLinkStatsDeviceResponseDto> deviceStats = Collections.emptyList();
        List<ShortLinkStatsNetworkResponseDto> networkStats = Collections.emptyList();
        List<ShortLinkStatsUvResponseDto> uvTypeStats = Collections.emptyList();

        // ====================== 单个统计模块隔离，异常捕获不扩散 ======================
        try {
            totalMap = linkAccessStatsService.getPvUvUipTotal(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取PV/UV/UIP失败，fullShortUrl：{}", fullShortUrl, e);
        }

        // UV/UIP 按整个区间去重：小时表存的是增量值，跨小时/跨天累加会把同一访客重复计入
        try {
            Map<String, Integer> visitor = linkAccessLogStatsService.countDistinctVisitor(fullShortUrl, start, end);
            totalMap = new HashMap<>(totalMap);
            totalMap.put("uv", visitor.getOrDefault("uv", 0));
            totalMap.put("uip", visitor.getOrDefault("uip", 0));
        } catch (Exception e) {
            log.error("[统计接口] 获取去重访客数失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            daily = linkAccessStatsService.getDailyStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取每日统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            localeCnStats = linkLocaleStatsService.getLocaleCnStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取地区统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            hourStats = linkAccessStatsService.getHourStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取小时统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            topIpStats = linkAccessLogStatsService.getTopIpStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取TOP IP失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            weekdayStats = linkAccessStatsService.getWeekdayStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取星期统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            browserStats = linkBrowserStatsService.getBrowserStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取浏览器统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            osStats = linkOsStatsService.getOsStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取操作系统统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            deviceStats = linkDeviceStatsService.getDeviceStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取设备统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            networkStats = linkNetworkStatsService.getNetworkStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取网络类型统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        try {
            uvTypeStats = linkAccessLogStatsService.getUvTypeStats(fullShortUrl, start, end);
        } catch (Exception e) {
            log.error("[统计接口] 获取新老访客统计失败，fullShortUrl：{}", fullShortUrl, e);
        }

        // ====================== 构建返回值 ======================
        return ShortLinkStatsRespDTO.builder()
                .pv(totalMap.get("pv"))
                .uv(totalMap.get("uv"))
                .uip(totalMap.get("uip"))
                .daily(daily)
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .uvTypeStats(uvTypeStats)
                .build();
    }

    @Override
    public ShortLinkStatsRespDTO groupShortLinkStats(ShortLinkGroupStatsRequestDto groupStatsRequest) {
        // 1. 参数校验（核心参数，必须校验，不隔离）
        if (groupStatsRequest == null) {
            throw new ServiceException("分组统计请求不能为空");
        }
        String gid = groupStatsRequest.getGid();
        if (gid == null || gid.isBlank()) {
            throw new ServiceException("分组标识不能为空");
        }

        // 2. 日期解析与校验
        LocalDateTime start = groupStatsRequest.getStartDate();
        LocalDateTime end = groupStatsRequest.getEndDate();
        if (start == null || end == null) {
            throw new ServiceException("日期不能为空");
        }
        if (start.isAfter(end)) {
            throw new ServiceException("开始日期不能晚于结束日期");
        }

        // 3. 查询该分组下所有有效的短链接
        List<String> fullShortUrls;
        try {
            fullShortUrls = linkMapper.selectFullShortUrlsByGid(gid);
        } catch (Exception e) {
            log.error("[分组统计] 查询分组下短链接失败，gid：{}", gid, e);
            fullShortUrls = Collections.emptyList();
        }
        // 无有效短链接，直接返回空数据
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return ShortLinkStatsRespDTO.builder()
                    .pv(0)
                    .uv(0)
                    .uip(0)
                    .daily(Collections.emptyList())
                    .localeCnStats(Collections.emptyList())
                    .hourStats(new ArrayList<>(Collections.nCopies(24, 0)))
                    .topIpStats(Collections.emptyList())
                    .weekdayStats(new ArrayList<>(Collections.nCopies(7, 0)))
                    .browserStats(Collections.emptyList())
                    .osStats(Collections.emptyList())
                    .deviceStats(Collections.emptyList())
                    .networkStats(Collections.emptyList())
                    .uvTypeStats(Collections.emptyList())
                    .build();
        }

        // ====================== 初始化默认值 ======================
        Map<String, Integer> totalMap = new HashMap<>();
        totalMap.put("pv", 0);
        totalMap.put("uv", 0);
        totalMap.put("uip", 0);

        List<ShortLinkStatsAccessDailyResponseDto> daily = Collections.emptyList();
        List<ShortLinkStatsLocaleCNResponseDto> localeCnStats = Collections.emptyList();
        List<Integer> hourStats = new ArrayList<>(Collections.nCopies(24, 0));
        List<ShortLinkStatsTopIpResponseDto> topIpStats = Collections.emptyList();
        List<Integer> weekdayStats = new ArrayList<>(Collections.nCopies(7, 0));
        List<ShortLinkStatsBrowserResponseDto> browserStats = Collections.emptyList();
        List<ShortLinkStatsOsResponseDto> osStats = Collections.emptyList();
        List<ShortLinkStatsDeviceResponseDto> deviceStats = Collections.emptyList();
        List<ShortLinkStatsNetworkResponseDto> networkStats = Collections.emptyList();
        List<ShortLinkStatsUvResponseDto> uvTypeStats = Collections.emptyList();

        // ====================== 批量统计：模块隔离容错 ======================
        try {
            totalMap = linkAccessStatsService.getPvUvUipTotalBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取PV/UV/UIP失败，gid：{}", gid, e);
        }

        // UV/UIP 按整个区间去重：小时表存的是增量值，跨小时/跨天累加会把同一访客重复计入
        try {
            Map<String, Integer> visitor = linkAccessLogStatsService.countDistinctVisitorBatch(fullShortUrls, start, end);
            totalMap = new HashMap<>(totalMap);
            totalMap.put("uv", visitor.getOrDefault("uv", 0));
            totalMap.put("uip", visitor.getOrDefault("uip", 0));
        } catch (Exception e) {
            log.error("[分组统计] 获取去重访客数失败，gid：{}", gid, e);
        }

        try {
            daily = linkAccessStatsService.getDailyStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取每日统计失败，gid：{}", gid, e);
        }

        try {
            localeCnStats = linkLocaleStatsService.getLocaleCnStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取地区统计失败，gid：{}", gid, e);
        }

        try {
            hourStats = linkAccessStatsService.getHourStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取小时统计失败，gid：{}", gid, e);
        }

        try {
            topIpStats = linkAccessLogStatsService.getTopIpStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取TOP IP失败，gid：{}", gid, e);
        }

        try {
            weekdayStats = linkAccessStatsService.getWeekdayStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取星期统计失败，gid：{}", gid, e);
        }

        try {
            browserStats = linkBrowserStatsService.getBrowserStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取浏览器统计失败，gid：{}", gid, e);
        }

        try {
            osStats = linkOsStatsService.getOsStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取操作系统统计失败，gid：{}", gid, e);
        }

        try {
            deviceStats = linkDeviceStatsService.getDeviceStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取设备统计失败，gid：{}", gid, e);
        }

        try {
            networkStats = linkNetworkStatsService.getNetworkStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取网络类型统计失败，gid：{}", gid, e);
        }

        try {
            uvTypeStats = linkAccessLogStatsService.getUvTypeStatsBatch(fullShortUrls, start, end);
        } catch (Exception e) {
            log.error("[分组统计] 获取新老访客统计失败，gid：{}", gid, e);
        }

        // 5. 封装返回结果
        return ShortLinkStatsRespDTO.builder()
                .pv(totalMap.get("pv"))
                .uv(totalMap.get("uv"))
                .uip(totalMap.get("uip"))
                .daily(daily)
                .localeCnStats(localeCnStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .uvTypeStats(uvTypeStats)
                .build();
    }

    @Override
    public PageResult<ShortLinkStatsAccessRecordResponseDto> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordRequestDto accessRecordRequest) {
        return linkAccessLogStatsService.getAccessRecordPage(accessRecordRequest);
    }

    @Override
    public PageResult<ShortLinkStatsAccessRecordResponseDto> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordRequestDto groupRequest) {
        // 分页查询也增加容错，防止单条报错
        try {
            ShortLinkStatsAccessRecordRequestDto accessRecordRequest = new ShortLinkStatsAccessRecordRequestDto();
            accessRecordRequest.setGid(groupRequest.getGid());
            accessRecordRequest.setCurrent(groupRequest.getCurrent());
            accessRecordRequest.setSize(groupRequest.getSize());
            accessRecordRequest.setStartDate(groupRequest.getStartDate());
            accessRecordRequest.setEndDate(groupRequest.getEndDate());
            return linkAccessLogStatsService.getAccessRecordPage(accessRecordRequest);
        } catch (Exception e) {
            log.error("[分组统计] 获取访问记录分页失败，gid：{}", groupRequest.getGid(), e);
            // 返回空分页数据
            return new PageResult<>(groupRequest.getSize(), groupRequest.getCurrent(), 0L, 0L, Collections.emptyList());
        }
    }
}
