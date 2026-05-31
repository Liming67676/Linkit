package org.hang.shortlink.project.dto.stats.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链访问记录统计请求参数
 */
@Data
@Schema(name = "短链接访问记录统计请求参数", description = "查询单个短链接访问记录的请求参数")
public class ShortLinkStatsAccessRecordRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页码")
    private Long current;
    
    @Schema(description = "每页条数")
    private Long size;

    @Schema(description = "完整短链接")
    private String fullShortUrl;
    
    @Schema(description = "分组标识")
    private String gid;
    
    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;
    
    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;
}
