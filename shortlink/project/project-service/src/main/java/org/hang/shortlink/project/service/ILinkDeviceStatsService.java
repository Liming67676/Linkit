package org.hang.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsDeviceResponseDto;
import org.hang.shortlink.project.pojo.po.LinkDeviceStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短链接设备统计服务
 */
public interface ILinkDeviceStatsService extends IService<LinkDeviceStats> {

    /**
     * 更新短链接设备统计信息
     *
     * @param fullShortUrl 短链接全地址
     * @param request      HTTP请求
     */
    void updateLinkDeviceStats(String fullShortUrl, String device);

    /**
     * 获取短链接设备统计信息
     *
     * @param fullShortUrl 短链接全地址
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接设备统计信息
     */
    List<ShortLinkStatsDeviceResponseDto> getDeviceStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接设备统计信息
     *
     * @param fullShortUrls 短链接全地址列表
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接设备统计信息
     */
    List<ShortLinkStatsDeviceResponseDto> getDeviceStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);
}