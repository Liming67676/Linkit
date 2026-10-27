package org.hang.shortlink.project.service;

import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.common.pojo.PageResult;
import org.hang.shortlink.project.dto.stats.request.ShortLinkStatsAccessRecordRequestDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsAccessRecordResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsTopIpResponseDto;
import org.hang.shortlink.project.dto.stats.response.ShortLinkStatsUvResponseDto;
import org.hang.shortlink.project.pojo.po.LinkAccessLogStats;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 短链接全维度访问日志统计表 服务类
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
public interface ILinkAccessLogStatsService extends IService<LinkAccessLogStats> {

    /**
     * 插入访问日志
     * @param fullShortUrl 短链接全地址
     * @param request      HTTP请求
     */
    void insertAccessLog(String fullShortUrl, String ip, String osName
            ,String browser,String device,String network);


    /**
     * 获取短链接Top IP统计信息
     *
     * @param fullShortUrl 短链接全地址
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接Top IP统计信息
     */

    List<ShortLinkStatsTopIpResponseDto> getTopIpStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);


    /**
     * 获取短链接 -- 新老访客统计
     *
     * @param fullShortUrl 短链接全地址
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链拉UV统计信息
     */
    List<ShortLinkStatsUvResponseDto> getUvTypeStats(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接Top IP统计信息
     *
     * @param fullShortUrls 短链接全地址列表
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链接Top IP统计信息
     */
    List<ShortLinkStatsTopIpResponseDto> getTopIpStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

    /**
     * 批量获取短链接 -- 新老访客统计
     *
     * @param fullShortUrls 短链接全地址列表
     * @param start        开始时间
     * @param end          结束时间
     * @return 短链拉UV统计信息
     */
    List<ShortLinkStatsUvResponseDto> getUvTypeStatsBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);

    /**
     * 区间内去重访客数（跨天去重，返回 uv / uip）
     */
    java.util.Map<String, Integer> countDistinctVisitor(String fullShortUrl, LocalDateTime start, LocalDateTime end);

    /**
     * 批量：区间内去重访客数（跨天去重，返回 uv / uip）
     */
    java.util.Map<String, Integer> countDistinctVisitorBatch(List<String> fullShortUrls, LocalDateTime start, LocalDateTime end);


    /**
     * 分页查询访问记录（支持 单个短链接 / 分组GID 查询）
     */
    PageResult<ShortLinkStatsAccessRecordResponseDto> getAccessRecordPage(ShortLinkStatsAccessRecordRequestDto request);
}
