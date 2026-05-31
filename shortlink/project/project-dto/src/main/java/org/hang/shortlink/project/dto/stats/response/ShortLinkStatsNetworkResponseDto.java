package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按网络
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "网络类型统计响应", description = "按访问网络类型统计的数据")
public class ShortLinkStatsNetworkResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 统计
     */
    @Schema(description = "访问数量")
    private Integer cnt;

    /**
     * 访问网络
     */
    @Schema(description = "网络类型")
    private String network;

    /**
     * 占比
     */
    @Schema(description = "占比百分比")
    private Double ratio;
}