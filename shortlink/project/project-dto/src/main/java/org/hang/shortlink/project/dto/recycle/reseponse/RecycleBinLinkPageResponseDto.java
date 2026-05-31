package org.hang.shortlink.project.dto.recycle.reseponse;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "回收站短链接分页响应", description = "分页查询回收站短链接返回的信息")
public class RecycleBinLinkPageResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "ID")
    private Long id;

    /**
     * 域名
     */
    @Schema(description = "域名")
    private String domain;

    /**
     * 短链接
     */
    @Schema(description = "短链接后缀")
    private String shortUri;

    /**
     * 完整短链接
     */
    @Schema(description = "完整短链接")
    private String fullShortUrl;

    /**
     * 原始链接
     */
    @Schema(description = "原始链接")
    private String originUrl;

    /**
     * 点击量
     */
    @Schema(description = "点击量")
    private Integer clickNum;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识")
    private String gid;

    /**
     * 启用标识 0：已启用 1：未启用
     */
    @Schema(description = "启用标识，0：已启用 1：未启用")
    private Integer enableStatus;

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

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 站点图标
     */
    @Schema(description = "站点图标")
    private String favicon;
}
