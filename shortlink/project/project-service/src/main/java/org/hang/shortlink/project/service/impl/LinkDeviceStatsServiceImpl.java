package org.hang.shortlink.project.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsDeviceResponseDto;
import org.hang.shortlink.project.mapper.LinkDeviceStatsMapper;
import org.hang.shortlink.project.pojo.po.LinkDeviceStats;
import org.hang.shortlink.project.service.ILinkDeviceStatsService;
import org.hang.shortlink.project.utils.DeviceUtils;
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
public class LinkDeviceStatsServiceImpl extends ServiceImpl<LinkDeviceStatsMapper, LinkDeviceStats> implements ILinkDeviceStatsService {

    private final LinkDeviceStatsMapper linkDeviceStatsMapper;


    @Override
    public void updateLinkDeviceStats(String fullShortUrl, String device) {
        LocalDate now = LocalDate.now();

        LinkDeviceStats deviceStats = lambdaQuery()
                .eq(LinkDeviceStats::getFullShortUrl, fullShortUrl)
                .eq(LinkDeviceStats::getDate, now)
                .eq(LinkDeviceStats::getDevice, device)
                .one();

        if (deviceStats == null) {
            deviceStats = LinkDeviceStats.builder()
                    .fullShortUrl(fullShortUrl)
                    .date(now)
                    .device(device)
                    .cnt(1)
                    .build();
            save(deviceStats);
        } else {
            lambdaUpdate()
                    .eq(LinkDeviceStats::getFullShortUrl, fullShortUrl)
                    .eq(LinkDeviceStats::getDate, now)
                    .eq(LinkDeviceStats::getDevice, device)
                    .set(LinkDeviceStats::getUpdateTime, LocalDateTime.now())
                    .setSql("cnt = cnt + 1")
                    .update();
        }
    }

    @Override
    public List<ShortLinkStatsDeviceResponseDto> getDeviceStats(String fullShortUrl, LocalDateTime start, LocalDateTime end) {
        List<Map<String, Object>> deviceStats = linkDeviceStatsMapper.selectDeviceStatsWithRatio(fullShortUrl, start, end);
        if (deviceStats.isEmpty()) {
            return Collections.emptyList();
        }
        return deviceStats.stream()
                .map(map -> ShortLinkStatsDeviceResponseDto.builder()
                        .device((String) map.get("device"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ShortLinkStatsDeviceResponseDto> getDeviceStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end) {
        if (fullShortUrls == null || fullShortUrls.isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> deviceStats = linkDeviceStatsMapper.selectDeviceStatsWithRatioBatch(fullShortUrls, start, end);
        if (deviceStats.isEmpty()) return Collections.emptyList();
        return deviceStats.stream()
                .map(map -> ShortLinkStatsDeviceResponseDto.builder()
                        .device((String) map.get("device"))
                        .cnt(((Number) map.get("total_cnt")).intValue())
                        .ratio(Optional.ofNullable(map.get("ratio_value"))
                                .map(Number.class::cast)
                                .map(Number::doubleValue)
                                .orElse(0.0))
                        .build())
                .collect(Collectors.toList());
    }
}