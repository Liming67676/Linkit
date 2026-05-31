package org.hang.shortlink.project.dto.link.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接创建请求参数", description = "创建短链接的请求参数")
public class LinkCreateRequestDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domain;



    /**
     * 创建类型 0：接口 1：控制台
     */
    @Schema(description = "创建类型，0：接口 1：控制台")
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    @Schema(description = "有效期类型，0：永久有效 1：用户自定义")
    private Integer validDateType;

    /**
     * 有效期
     */
    @Schema(description = "有效期")
    private LocalDateTime validDate;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String describe;


    // 下面是查询条件字段

    /**
     * 原始链接，由此生成短链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;
}
