package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按天
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接每日访问统计响应", description = "按天统计的短链接访问数据")
public class ShortLinkStatsAccessDailyResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 日期
     */
    @Schema(description = "日期")
    private String date;

    /**
     * 访问量
     */
    @Schema(description = "访问量(PV)")
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
}