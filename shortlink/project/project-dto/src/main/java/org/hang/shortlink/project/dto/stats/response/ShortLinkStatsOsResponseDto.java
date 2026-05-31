package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按操作系统
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "操作系统统计响应", description = "按操作系统类型统计的数据")
public class ShortLinkStatsOsResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 统计
     */
    @Schema(description = "访问数量")
    private Integer cnt;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统名称")
    private String os;

    /**
     * 占比
     */
    @Schema(description = "占比百分比")
    private Double ratio;
}