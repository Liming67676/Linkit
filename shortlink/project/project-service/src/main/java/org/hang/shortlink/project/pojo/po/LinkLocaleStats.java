package org.hang.shortlink.project.pojo.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短链接访问地区信息表
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_locale_stats")
public class LinkLocaleStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 国家标识
     */
    private String country;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 市名称
     */
    private String city;

    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 日期
     */
    private LocalDate date;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识 0删除 1未删除
     */
    @TableLogic(value = "0", delval = "1")
    @Builder.Default
    private Boolean delFlag = false;


}
