package org.hang.shortlink.admin.pojo.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "用户登录响应", description = "用户登录成功后返回的令牌信息")
public class UserLoginRespDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户ID")
    private Long id;

//    private String username;

    @Schema(description = "登录令牌")
    private String token;

}
