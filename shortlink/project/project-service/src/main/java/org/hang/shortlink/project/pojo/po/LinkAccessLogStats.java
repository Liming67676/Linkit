package org.hang.shortlink.project.pojo.po;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 短链接全维度访问日志统计表
 * </p>
 *
 * @author zhanghang1216
 * @since 2026-04-26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_log_stats")
public class LinkAccessLogStats implements Serializable {

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
     * UV类型：新用户/老用户
     */
    private String uvType;

    /**
     * 用户信息
     */
    private String user;


    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 操作系统
     */
    private String os;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 设备类型：PC/手机/平板
     */
    private String device;

    /**
     * 网络类型：WIFI/4G/5G
     */
    private String network;


    /**
     * IP地址
     */
    private String ip;



    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
