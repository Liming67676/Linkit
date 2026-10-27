package org.hang.shortlink.project.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.project.constant.RedisConstant;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessDailyResponseDto;
import org.hang.shortlink.project.pojo.po.LinkAccessStats;
import org.hang.shortlink.project.mapper.LinkAccessStatsMapper;
import org.hang.shortlink.project.service.ILinkAccessStatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.project.utils.CookieUserUtil;
import org.hang.shortlink.project.utils.IpUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 短链接访问基础属性表 服务实现类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@Service
@RequiredArgsConstructor
public class LinkAccessStatsServiceImpl extends ServiceImpl<LinkAccessStatsMapper, LinkAccessStats> implements ILinkAccessStatsService {

    private final StringRedisTemplate redisTemplate;

    private final LinkAccessStatsMapper linkAccessStatsMapper;

    private final IpUtils ipUtils;


    @Override
    public void updateLinkAccessStats(String fullShortUrl, HttpServletRequest request, HttpServletResponse response) {
        // full_short_url + date + hour 是一条数据最小标识

        // 1. 获取时间维度：日期 + 小时 + 星期
        LocalDateTime now = LocalDateTime.now();
        LocalDate date = now.toLocalDate();
        int hour = now.getHour();
        int weekday = now.getDayOfWeek().getValue();

        // 2. 获取 小时级 增量
        int uvIncrement = countHourlyUv(fullShortUrl, date, request, response);
        int uipIncrement = countHourlyUip(fullShortUrl, date, request);
        // PV 每次必 +1
        int pvIncrement = 1;

        // 3. 查询当前小时是否已有统计数据
        LinkAccessStats stats = lambdaQuery()
                .eq(LinkAccessStats::getFullShortUrl, fullShortUrl)
                .eq(LinkAccessStats::getDate, date)
                .eq(LinkAccessStats::getHour, hour)
                .one();

        if (stats == null) {
            // 4. 无数据：初始化插入（第一次访问）
            stats = LinkAccessStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .hour(hour)
                    .weekday(weekday)
                    .pv(pvIncrement)
                    .uv(uvIncrement)
                    .uip(uipIncrement)
                    .build();
            save(stats);
        } else {
            // 5. 有数据：增量更新
            lambdaUpdate()
                    .eq(LinkAccessStats::getId, stats.getId())
                    .set(LinkAccessStats::getUpdateTime, LocalDateTime.now())
                    .setSql("pv = pv + " + pvIncrement)
                    .setSql("uv = uv + " + uvIncrement)
                    .setSql("uip = uip + " + uipIncrement)
                    .update();
        }
    }

    /**
     * 【小时级】统计UV增量：1=新用户，0=老用户
     */
    private Integer countHourlyUv(String fullShortUrl, LocalDate date,
                                  HttpServletRequest request, HttpServletResponse response) {

        String redisKey = String.format(RedisConstant.UV_HOURLY_KEY, fullShortUrl, date);
        String cookieName = "uv";

        // 获取/生成Cookie
        String userCookie = CookieUserUtil.getUserIdentifier(request, cookieName);
        if (userCookie == null) {
            userCookie = CookieUserUtil.generateUserCookie(response, cookieName, RedisConstant.EXPIRE_DAY_SECONDS);
        }

        // 原子去重：新增返回1，已存在返回0
        Long add = redisTemplate.opsForSet().add(redisKey, userCookie);
        redisTemplate.expire(redisKey, RedisConstant.EXPIRE_DAY_SECONDS, TimeUnit.SECONDS);
        return add != null && add > 0 ? 1 : 0;
    }

    /**
     * 【小时级】统计UIP增量：1=新IP，0=老IP
     */
    private Integer countHourlyUip(String fullShortUrl, LocalDate date, HttpServletRequest request) {
        String redisKey = String.format(RedisConstant.UIP_HOURLY_KEY, fullShortUrl, date);
        // 获取客户端真实IP
        String clientIp = ipUtils.getClientIp(request);

        // 原子去重
        Long add = redisTemplate.opsForSet().add(redisKey, clientIp);
        redisTemplate.expire(redisKey, RedisConstant.EXPIRE_DAY_SECONDS, TimeUnit.SECONDS);
        return add != null && add > 0 ? 1 : 0;
    }




    @Override
    public Map<String, Integer> getPvUvUipTotal(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        Map<String, Object> map = linkAccessStatsMapper.selectTotalPvUvUip(fullShortUrl, start, end);
        if (map == null || map.isEmpty()) {
            return Map.of("pv", 0, "uv", 0, "uip", 0);
        }

        int pv = Optional.ofNullable((Number) map.get("total_pv")).map(Number::intValue).orElse(0);
        int uv = Optional.ofNullable((Number) map.get("total_uv")).map(Number::intValue).orElse(0);
        int uip = Optional.ofNullable((Number) map.get("total_uip")).map(Number::intValue).orElse(0);
        return Map.of("pv", pv, "uv", uv, "uip", uip);
    }

    @Override
    public List<ShortLinkStatsAccessDailyResponseDto> getDailyStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> list = linkAccessStatsMapper.selectDailyStats(fullShortUrl, start, end);
        if (list.isEmpty()) return Collections.emptyList();

        return list.stream().map(map -> ShortLinkStatsAccessDailyResponseDto.builder()
                .date(map.get("date").toString())
                .pv(((Number) map.get("pv")).intValue())
                .uv(((Number) map.get("uv")).intValue())
                .uip(((Number) map.get("uip")).intValue())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getHourStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Integer> hourStats = new ArrayList<>(Collections.nCopies(24, 0));
        List<Map<String, Object>> list = linkAccessStatsMapper.selectHourStats(fullShortUrl, start, end);

        list.forEach(map -> {
            int hour = (Integer) map.get("hour");
            int count = ((Number) map.get("pv")).intValue();
            hourStats.set(hour, count);
        });
        return hourStats;
    }

    @Override
    public List<Integer> getWeekdayStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Integer> weekdayStats = new ArrayList<>(Collections.nCopies(7, 0));
        List<Map<String, Object>> list = linkAccessStatsMapper.selectWeekdayStats(fullShortUrl, start, end);

        list.forEach(map -> {
            int weekday = (Integer) map.get("weekday");
            int count = ((Number) map.get("pv")).intValue();
            // weekday 落库存的是 1-7（周一至周日），列表下标 0-6 对应周一至周日
            if (weekday >= 1 && weekday <= 7) {
                weekdayStats.set(weekday - 1, count);
            }
        });
        return weekdayStats;
    }

    // ========== 批量短链接统计实现 ==========
    @Override
    public Map<String, Integer> getPvUvUipTotalBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Map.of("pv", 0, "uv", 0, "uip", 0);
        }
        Map<String, Object> map = linkAccessStatsMapper.selectTotalPvUvUipBatch(fullShortUrls, start, end);
        if (map == null || map.isEmpty()) {
            return Map.of("pv", 0, "uv", 0, "uip", 0);
        }
        int pv = Optional.ofNullable((Number) map.get("total_pv")).map(Number::intValue).orElse(0);
        int uv = Optional.ofNullable((Number) map.get("total_uv")).map(Number::intValue).orElse(0);
        int uip = Optional.ofNullable((Number) map.get("total_uip")).map(Number::intValue).orElse(0);
        return Map.of("pv", pv, "uv", uv, "uip", uip);
    }

    @Override
    public List<ShortLinkStatsAccessDailyResponseDto> getDailyStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = linkAccessStatsMapper.selectDailyStatsBatch(fullShortUrls, start, end);
        if (list.isEmpty()) return Collections.emptyList();
        return list.stream().map(map -> ShortLinkStatsAccessDailyResponseDto.builder()
                .date(map.get("date").toString())
                .pv(((Number) map.get("pv")).intValue())
                .uv(((Number) map.get("uv")).intValue())
                .uip(((Number) map.get("uip")).intValue())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<Integer> getHourStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return new ArrayList<>(Collections.nCopies(24, 0));
        }
        List<Integer> hourStats = new ArrayList<>(Collections.nCopies(24, 0));
        List<Map<String, Object>> list = linkAccessStatsMapper.selectHourStatsBatch(fullShortUrls, start, end);
        list.forEach(map -> {
            int hour = (Integer) map.get("hour");
            int count = ((Number) map.get("pv")).intValue();
            hourStats.set(hour, count);
        });
        return hourStats;
    }

    @Override
    public List<Integer> getWeekdayStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return new ArrayList<>(Collections.nCopies(7, 0));
        }
        List<Integer> weekdayStats = new ArrayList<>(Collections.nCopies(7, 0));
        List<Map<String, Object>> list = linkAccessStatsMapper.selectWeekdayStatsBatch(fullShortUrls, start, end);
        list.forEach(map -> {
            int weekday = (Integer) map.get("weekday");
            int count = ((Number) map.get("pv")).intValue();
            // weekday 落库存的是 1-7（周一至周日），列表下标 0-6 对应周一至周日
            if (weekday >= 1 && weekday <= 7) {
                weekdayStats.set(weekday - 1, count);
            }
        });
        return weekdayStats;
    }


}
