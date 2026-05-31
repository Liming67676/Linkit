package org.hang.shortlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsOsResponseDto;
import org.hang.shortlink.project.pojo.po.LinkOsStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description  短链接访问操作系统信息统计服务接口
 */
public interface ILinkOsStatsService extends IService<LinkOsStats> {

    /**
     * 更新短链接访问操作系统信息统计
     * @param fullShortUrl 完整短链接
     * @param request HTTP请求
     */
    void updateLinkOsStats(String fullShortUrl, String osName);

     /**
      * 获取短链接访问操作系统信息统计
      * @param fullShortUrl 完整短链接
      * @param start 开始时间
      * @param end 结束时间
      * @return 短链接访问操作系统信息统计
      */
    List<ShortLinkStatsOsResponseDto> getOsStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接访问操作系统信息统计
     * @param fullShortUrls 完整短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接访问操作系统信息统计
     */
    List<ShortLinkStatsOsResponseDto> getOsStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);
}
