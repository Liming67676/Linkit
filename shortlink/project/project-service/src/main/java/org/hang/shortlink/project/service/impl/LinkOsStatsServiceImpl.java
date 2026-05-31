package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsOsResponseDto;
import org.hang.shortlink.project.mapper.LinkOsStatsMapper;
import org.hang.shortlink.project.pojo.po.LinkOsStats;
import org.hang.shortlink.project.service.ILinkOsStatsService;
import org.hang.shortlink.project.utils.OsUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Service
@RequiredArgsConstructor
public class LinkOsStatsServiceImpl extends ServiceImpl<LinkOsStatsMapper, LinkOsStats> implements ILinkOsStatsService {

    private final LinkOsStatsMapper linkOsStatsMapper;


    /**
     * 更新短链接访问操作系统信息统计
     * `full_short_url`, `date`, `os`, `del_flag` 唯一索引
     */
    @Override
    public void updateLinkOsStats(String fullShortUrl, String osName) {
        LocalDate now = LocalDate.now();
        // 1. 查询是否记录
        LinkOsStats osStats = lambdaQuery().eq(LinkOsStats::getFullShortUrl, fullShortUrl)
                .eq(LinkOsStats::getDate, now)
                .eq(LinkOsStats::getOs, osName)
                .one();
        if (osStats == null) {
            // 2. 不存在，插入新记录
            osStats = LinkOsStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .os(osName)
                    .cnt(1)
                    .build();
            save(osStats);
        } else {
            // 3. 存在，更新记录
            lambdaUpdate().eq(LinkOsStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkOsStats::getDate, now)
                    .eq(LinkOsStats::getOs, osName)
                    .set(LinkOsStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1")
                    .update();
        }
    }


    @Override
    public List<ShortLinkStatsOsResponseDto> getOsStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> osStats = linkOsStatsMapper.selectOsStatsWithRatio(fullShortUrl, start, end);
        if (osStats.isEmpty()) {
            return Collections.emptyList();
        }
        return osStats.stream()
                .map(map -> ShortLinkStatsOsResponseDto.builder()
                        .os((String) map.get("os"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsOsResponseDto> getOsStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> osStats = linkOsStatsMapper.selectOsStatsWithRatioBatch(fullShortUrls, start, end);
        if (osStats.isEmpty()) return Collections.emptyList();
        return osStats.stream()
                .map(map -> ShortLinkStatsOsResponseDto.builder()
                        .os((String) map.get("os"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }

}
