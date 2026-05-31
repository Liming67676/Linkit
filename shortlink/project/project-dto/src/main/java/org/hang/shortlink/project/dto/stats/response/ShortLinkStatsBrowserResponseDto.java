package org.hang.shortlink.project.dto.stats.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 短链统计访问详情-按浏览器
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "浏览器统计响应", description = "按浏览器类型统计的访问数据")
public class ShortLinkStatsBrowserResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 统计
     */
    @Schema(description = "访问数量")
    private Integer cnt;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器名称")
    private String browser;

    /**
     * 占比
     */
    @Schema(description = "占比百分比")
    private Double ratio;
}