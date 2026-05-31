package org.hang.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsNetworkResponseDto;
import org.hang.shortlink.project.pojo.po.LinkNetworkStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 短链接网络统计服务
 */
public interface ILinkNetworkStatsService extends IService<LinkNetworkStats> {

    /**
     * 更新短链接网络统计信息
     *
     * @param fullShortUrl 短链接全地址
     * @param request      HTTP请求
     */
    void updateLinkNetworkStats(String fullShortUrl, String networkType);


    /**
     * 获取短链接网络统计信息
     *
     * @param fullShortUrl 短链接全地址
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接网络统计信息
     */
    List<ShortLinkStatsNetworkResponseDto> getNetworkStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接网络统计信息
     *
     * @param fullShortUrls 短链接全地址列表
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接网络统计信息
     */
    List<ShortLinkStatsNetworkResponseDto> getNetworkStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);
}