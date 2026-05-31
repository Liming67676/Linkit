package org.hang.shortlink.project.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link")
@Schema(name = "短链接实体", description = "短链接信息实体类")
public class Link implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "短链接ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
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
    @TableField("`describe`")
    private String describe;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 站点图标
     */
    @Schema(description = "站点图标")
    private String favicon;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @Schema(description = "删除标识，0：未删除 1：已删除")
    @TableLogic
    @Builder.Default
    private Boolean delFlag = false;
}
