package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接统计响应", description = "短链接完整统计数据响应")
public class ShortLinkStatsRespDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 访问量
     */
    @Schema(description = "总访问量(PV)")
    private Integer pv;

    /**
     * 独立访客数
     */
    @Schema(description = "独立访客数(UV)")
    private Integer uv;

    /**
     * 独立IP数
     */
    @Schema(description = "独立IP数(UIP)")
    private Integer uip;

    /**
     * 基础访问详情
     */
    @Schema(description = "每日访问统计列表")
    private List<ShortLinkStatsAccessDailyResponseDto> daily;

    /**
     * 地区访问详情（仅国内）
     */
    @Schema(description = "地区访问统计列表")
    private List<ShortLinkStatsLocaleCNResponseDto> localeCnStats;

    /**
     * 小时访问详情
     */
    @Schema(description = "24小时访问统计列表")
    private List<Integer> hourStats;

    /**
     * 高频访问IP详情
     */
    @Schema(description = "高频访问IP列表")
    private List<ShortLinkStatsTopIpResponseDto> topIpStats;

    /**
     * 一周访问详情
     */
    @Schema(description = "一周7天访问统计列表")
    private List<Integer> weekdayStats;

    /**
     * 浏览器访问详情
     */
    @Schema(description = "浏览器访问统计列表")
    private List<ShortLinkStatsBrowserResponseDto> browserStats;

    /**
     * 操作系统访问详情
     */
    @Schema(description = "操作系统访问统计列表")
    private List<ShortLinkStatsOsResponseDto> osStats;

    /**
     * 访客访问类型详情
     */
    @Schema(description = "访客类型统计列表")
    private List<ShortLinkStatsUvResponseDto> uvTypeStats;

    /**
     * 访问设备类型详情
     */
    @Schema(description = "设备类型访问统计列表")
    private List<ShortLinkStatsDeviceResponseDto> deviceStats;

    /**
     * 访问网络类型详情
     */
    @Schema(description = "网络类型访问统计列表")
    private List<ShortLinkStatsNetworkResponseDto> networkStats;
}