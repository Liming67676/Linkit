package org.hang.shortlink.admin.pojo.po;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.Accessors;

/**
 *
 * @author zhanghang1216
 * @since 2026-04-18
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_group")
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "分组实体", description = "短链接分组信息实体类")
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Schema(description = "分组ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 分组标识
     */
    @Schema(description = "分组标识(GID)")
    private String gid;

    /**
     * 分组名称
     */
    @Schema(description = "分组名称")
    private String name;

    /**
     * 创建分组用户名
     */
    @Schema(description = "创建分组的用户名")
    private String username;

    /**
     * 分组排序
     */
    @Schema(description = "分组排序号")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
    private LocalDateTime updateTime;

    /**
     * 删除标识 0：未删除 删除后写入当前记录ID，避免同名分组多次逻辑删除时唯一键冲突
     */
    @Schema(description = "删除标识，0：未删除 删除后为当前记录ID")
    @TableLogic(value = "0", delval = "id")
    @Builder.Default
    private Long delFlag = 0L;


}
