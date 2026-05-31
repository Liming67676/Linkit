package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsNetworkResponseDto;
import org.hang.shortlink.project.mapper.LinkNetworkStatsMapper;
import org.hang.shortlink.project.pojo.po.LinkNetworkStats;
import org.hang.shortlink.project.service.ILinkNetworkStatsService;
import org.hang.shortlink.project.utils.NetworkUtils;
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
public class LinkNetworkStatsServiceImpl extends ServiceImpl<LinkNetworkStatsMapper, LinkNetworkStats> implements ILinkNetworkStatsService {

    private final LinkNetworkStatsMapper linkNetworkStatsMapper;


    @Override
    public void updateLinkNetworkStats(String fullShortUrl, String network) {
        LocalDate now = LocalDate.now();

        LinkNetworkStats networkStats = lambdaQuery()
                .eq(LinkNetworkStats::getFullShortUrl, fullShortUrl)
                .eq(LinkNetworkStats::getDate, now)
                .eq(LinkNetworkStats::getNetwork, network)
                .one();

        if (networkStats == null) {
            networkStats = LinkNetworkStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .network(network)
                    .cnt(1)
                    .build();
            save(networkStats);
        } else {
            lambdaUpdate()
                    .eq(LinkNetworkStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkNetworkStats::getDate, now)
                    .eq(LinkNetworkStats::getNetwork, network)
                    .set(LinkNetworkStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1")
                    .update();
        }
    }

    @Override
    public List<ShortLinkStatsNetworkResponseDto> getNetworkStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> networkStats = linkNetworkStatsMapper.selectNetworkStatsWithRatio(fullShortUrl, start, end);
        if (networkStats.isEmpty()) {
            return Collections.emptyList();
        }
        return networkStats.stream()
                .map(map -> ShortLinkStatsNetworkResponseDto.builder()
                        .network((String) map.get("network"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsNetworkResponseDto> getNetworkStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> networkStats = linkNetworkStatsMapper.selectNetworkStatsWithRatioBatch(fullShortUrls, start, end);
        if (networkStats.isEmpty()) return Collections.emptyList();
        return networkStats.stream()
                .map(map -> ShortLinkStatsNetworkResponseDto.builder()
                        .network((String) map.get("network"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }
}