package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按设备
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "设备类型统计响应", description = "按访问设备类型统计的数据")
public class ShortLinkStatsDeviceResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 统计
     */
    @Schema(description = "访问数量")
    private Integer cnt;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型")
    private String device;

    /**
     * 占比
     */
    @Schema(description = "占比百分比")
    private Double ratio;
}