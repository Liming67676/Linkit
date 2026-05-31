package org.hang.shortlink.project.dto.stats.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 短链接分组监控数据请求参数
 */
@Data
@Schema(name = "分组短链接统计请求参数", description = "查询分组短链接统计数据的请求参数")
public class ShortLinkGroupStatsRequestDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}