package org.hang.shortlink.project.dto.stats.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(name = "短链接统计请求参数", description = "查询单个短链接统计数据的请求参数")
public class ShortLinkStatsRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 完整短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;

//    /**
//     * 分组标识
//     */
//    private String gid;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 解决表单/GET参数转换
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 解决表单/GET参数转换
    private LocalDateTime endDate;

    /**
     * 启用标识 0：启用 1：未启用
     */
    @Schema(description = "启用标识，0：启用 1：未启用")
    private Integer enableStatus;
}