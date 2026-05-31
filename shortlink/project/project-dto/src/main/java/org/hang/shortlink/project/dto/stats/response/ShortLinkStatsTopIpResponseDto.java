package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 高频访问IP详情
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "高频IP统计响应", description = "高频访问IP统计信息")
public class ShortLinkStatsTopIpResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 统计
     */
    @Schema(description = "访问次数")
    private Integer cnt;

    /**
     * IP
     */
    @Schema(description = "IP地址")
    private String ip;
}