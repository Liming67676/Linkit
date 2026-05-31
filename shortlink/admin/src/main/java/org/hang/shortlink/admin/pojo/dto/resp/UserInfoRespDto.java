package org.hang.shortlink.admin.pojo.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hang.shortlink.common.serializer.MailDesensitizationSerializer;
import org.hang.shortlink.common.serializer.PhoneDesensitizationSerializer;
import org.hang.shortlink.common.serializer.UsernameDesensitizationSerializer;

import java.time.LocalDateTime;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 用户信息，返回
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "用户信息响应", description = "用户基本信息响应参数")
public class UserInfoRespDto {

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 密码
     */
//    private String password;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名")
    @JsonSerialize(using = UsernameDesensitizationSerializer.class)
    private String realName;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    @JsonSerialize(using = MailDesensitizationSerializer.class)
    private String mail;

    /**
     * 注销时间戳
     */
    @Schema(description = "注销时间戳")
    private Long deletionTime;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss ")
    private LocalDateTime updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @Schema(description = "删除标识，0：未删除 1：已删除")
    private Boolean delFlag;

}
