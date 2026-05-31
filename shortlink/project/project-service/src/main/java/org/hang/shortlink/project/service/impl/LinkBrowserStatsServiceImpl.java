package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsBrowserResponseDto;
import org.hang.shortlink.project.mapper.LinkBrowserStatsMapper;
import org.hang.shortlink.project.pojo.po.LinkBrowserStats;
import org.hang.shortlink.project.service.ILinkBrowserStatsService;
import org.hang.shortlink.project.utils.BrowserUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LinkBrowserStatsServiceImpl extends ServiceImpl<LinkBrowserStatsMapper, LinkBrowserStats> implements ILinkBrowserStatsService {


    @Override
    public void updateLinkBrowserStats(String fullShortUrl, String browser) {
        LocalDate now = LocalDate.now();

        LinkBrowserStats browserStats = lambdaQuery()
                .eq(LinkBrowserStats::getFullShortUrl, fullShortUrl)
                .eq(LinkBrowserStats::getDate, now)
                .eq(LinkBrowserStats::getBrowser, browser)
                .one();

        if (browserStats == null) {
            browserStats = LinkBrowserStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .browser(browser)
                    .cnt(1)
                    .build();
            save(browserStats);
        } else {
            lambdaUpdate()
                    .eq(LinkBrowserStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkBrowserStats::getDate, now)
                    .eq(LinkBrowserStats::getBrowser, browser)
                    .set(LinkBrowserStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1")
                    .update();
        }
    }

    @Override
    public List<ShortLinkStatsBrowserResponseDto> getBrowserStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {

        List<Map<String, Object>> browserStats = baseMapper.selectBrowserStatsWithRatio(fullShortUrl, start, end);

        if (browserStats.isEmpty()) {
            return Collections.emptyList();
        }

        // 封装DTO
        return browserStats.stream()
                .map(map -> ShortLinkStatsBrowserResponseDto.builder()
                        .browser((String) map.get("browser"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsBrowserResponseDto> getBrowserStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> browserStats = baseMapper.selectBrowserStatsWithRatioBatch(fullShortUrls, start, end);
        if (browserStats.isEmpty()) return Collections.emptyList();
        return browserStats.stream()
                .map(map -> ShortLinkStatsBrowserResponseDto.builder()
                        .browser((String) map.get("browser"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }
}