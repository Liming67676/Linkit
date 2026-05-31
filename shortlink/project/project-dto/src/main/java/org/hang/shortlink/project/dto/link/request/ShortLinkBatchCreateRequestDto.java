package org.hang.shortlink.project.dto.link.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接批量创建请求参数", description = "批量创建短链接的请求参数")
public class ShortLinkBatchCreateRequestDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 链接域名
     */
    @NotBlank(message = "域名不能为空")
    private String domain;

    /**
     * 原始链接集合
     */
    @Schema(description = "原始链接集合")
    private List<String> originUrls;

    /**
     * 描述集合
     */
    @Schema(description = "描述集合")
    private List<String> describes;


    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    @Schema(description = "创建类型，0：接口创建 1：控制台创建")
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    @Schema(description = "有效期类型，0：永久有效 1：自定义")
    private Integer validDateType;

    /**
     * 有效期
     */
    @Schema(description = "有效期")
    private LocalDateTime validDate;
}