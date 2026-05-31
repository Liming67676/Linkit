package org.hang.shortlink.project.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessDailyResponseDto;
import org.hang.shortlink.project.pojo.po.LinkAccessStats;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 短链接访问基础属性表 服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
public interface ILinkAccessStatsService extends IService<LinkAccessStats> {

    /**
     * 更新短链接访问统计信息
     * @param fullShortUrl 完整的短链接
     * @param request HTTP请求
     * @param response HTTP响应
     */
    void updateLinkAccessStats(String fullShortUrl, HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取短链接访问统计信息--  总PV/UV/UIP
     * @param fullShortUrl 完整的短链接
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接访问统计信息
     */
    Map<String, Integer> getPvUvUipTotal(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 获取短链接每日访问统计信息 -- 每日统计
     * @param fullShortUrl 完整的短链接
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接每日访问统计信息
     */
    List<ShortLinkStatsAccessDailyResponseDto> getDailyStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 获取短链接小时访问统计信息 -- 24小时统计
     * @param fullShortUrl 完整的短链接
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接小时访问统计信息
     */
    List<Integer> getHourStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 获取短链接星期访问统计信息 -- 星期统计
     * @param fullShortUrl 完整的短链接
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接星期访问统计信息
     */
    List<Integer> getWeekdayStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接访问统计信息 -- 总PV/UV/UIP
     * @param fullShortUrls 完整的短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接访问统计信息
     */
    Map<String, Integer> getPvUvUipTotalBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接每日访问统计信息 -- 每日统计
     * @param fullShortUrls 完整的短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接每日访问统计信息
     */
    List<ShortLinkStatsAccessDailyResponseDto> getDailyStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接小时访问统计信息 -- 24小时统计
     * @param fullShortUrls 完整的短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接小时访问统计信息
     */
    List<Integer> getHourStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接星期访问统计信息 -- 星期统计
     * @param fullShortUrls 完整的短链接列表
     * @param start 开始时间
     * @param end 结束时间
     * @return 短链接星期访问统计信息
     */
    List<Integer> getWeekdayStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);
}
