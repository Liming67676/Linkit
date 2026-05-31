package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按地区
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "地区统计响应", description = "按访问地区统计的数据")
public class ShortLinkStatsLocaleCNResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 统计
     */
    @Schema(description = "访问数量")
    private Integer cnt;

    /**
     * 地区
     */
    @Schema(description = "地区名称")
    private String locale;

    /**
     * 占比
     */
    @Schema(description = "占比百分比")
    private Double ratio;
}