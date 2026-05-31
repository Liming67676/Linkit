package org.hang.shortlink.project.dto.stats.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 短链接监控访问记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "短链接访问记录响应", description = "短链接访问记录详情")
public class ShortLinkStatsAccessRecordResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 访客类型
     */
    @Schema(description = "访客类型")
    private String uvType;

    /**
     * 浏览器
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * ip
     */
    @Schema(description = "IP地址")
    private String ip;

    /**
     * 访问网络
     */
    @Schema(description = "访问网络")
    private String network;

    /**
     * 访问设备
     */
    @Schema(description = "访问设备")
    private String device;

    /**
     * 地区
     */
    @Schema(description = "访问地区")
    private String locale;

    /**
     * 用户信息
     */
    @Schema(description = "用户信息")
    private String user;

    /**
     * 访问时间
     */
    @Schema(description = "访问时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
}