package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsLocaleCNResponseDto;
import org.hang.shortlink.project.pojo.dto.GaoDeIpLocationDTO;
import org.hang.shortlink.project.pojo.po.LinkLocaleStats;
import org.hang.shortlink.project.mapper.LinkLocaleStatsMapper;
import org.hang.shortlink.project.service.ILinkLocaleStatsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.hang.shortlink.project.utils.IpUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 短链接访问地区信息表 服务实现类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LinkLocaleStatsServiceImpl extends ServiceImpl<LinkLocaleStatsMapper, LinkLocaleStats> implements ILinkLocaleStatsService {

    // 定义未知地区常量
    private static final String UNKNOWN_ADCODE = "000000";
    private static final String UNKNOWN_REGION = "未知";

    private final IpUtils ipUtils;

    private final LinkLocaleStatsMapper linkLocaleStatsMapper;

    /**
     * 保存短链接访问地区信息
     * `full_short_url`,`date`,`adcode`,`del_flag` 是唯一索引
     */
    @Override
    public void updateLinkLocaleStats(String fullShortUrl, String ip) {

        LocalDate now = LocalDate.now();
        // 获取ip地址信息
        GaoDeIpLocationDTO location = ipUtils.getLocationByIp(ip);
        if (location == null) {
            log.warn("IP定位信息为空，已记录未知地区访问，ip: {}", ip);
            insertUnknownLocaleStats(fullShortUrl, now);
            return;
        }
        String adcode = location.getAdcode();
        // 1. 不存在这个信息
        LinkLocaleStats localeStats = lambdaQuery().eq(LinkLocaleStats::getFullShortUrl, fullShortUrl)
                .eq(LinkLocaleStats::getDate, now)
                .eq(LinkLocaleStats::getAdcode, adcode)
                .one();
        if (localeStats == null) {
            // 2. 不存在这个信息
            localeStats = LinkLocaleStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .adcode(adcode)
                    .country("中国") // 目前只有国内
                    .province(location.getProvince())
                    .city(location.getCity())
                    .cnt(1)
                    .build();
            save(localeStats);
        } else {
            // 3. 存在这个信息
            lambdaUpdate().eq(LinkLocaleStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkLocaleStats::getDate, now)
                    .eq(LinkLocaleStats::getAdcode, adcode)
                    .set(LinkLocaleStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1") //增量更新，防止并发问题
                    .update(localeStats);
        }
    }


    /**
     * 填充未知地区访问统计信息
     */
    private void insertUnknownLocaleStats(String fullShortUrl, LocalDate date) {
        // 查询是否已存在 今日+未知地区 的统计记录
        LinkLocaleStats unknownStats = lambdaQuery()
                .eq(LinkLocaleStats::getFullShortUrl, fullShortUrl)
                .eq(LinkLocaleStats::getDate, date)
                .eq(LinkLocaleStats::getAdcode, UNKNOWN_ADCODE)
                .one();

        if (unknownStats == null) {
            // 不存在 ,新增未知地区记录
            LinkLocaleStats insertStats = LinkLocaleStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(date)
                    .adcode(UNKNOWN_ADCODE)
                    .country(UNKNOWN_REGION)
                    .province(UNKNOWN_REGION)
                    .city(UNKNOWN_REGION)
                    .cnt(1)
                    .build();
            save(insertStats);
        } else {
            // 存在 ,并发安全 +1
            lambdaUpdate()
                    .eq(LinkLocaleStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkLocaleStats::getDate, date)
                    .eq(LinkLocaleStats::getAdcode, UNKNOWN_ADCODE)
                    .set(LinkLocaleStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1")
                    .update();
        }
    }


    @Override
    public List<ShortLinkStatsLocaleCNResponseDto> getLocaleCnStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> provinceStats = linkLocaleStatsMapper.selectProvinceStatsWithRatio(fullShortUrl, start, end);

        if (provinceStats.isEmpty()) {
            return Collections.emptyList();
        }

        return provinceStats.stream()
                .map(map -> ShortLinkStatsLocaleCNResponseDto.builder()
                        .locale((String) map.get("province"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public List<ShortLinkStatsLocaleCNResponseDto> getLocaleCnStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> provinceStats = linkLocaleStatsMapper.selectLocaleStatsWithRatioBatch(fullShortUrls, start, end);
        if (provinceStats.isEmpty()) return Collections.emptyList();
        return provinceStats.stream()
                .map(map -> ShortLinkStatsLocaleCNResponseDto.builder()
                        .locale((String) map.get("province"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }
}