package org.hang.shortlink.admin.utils;//package org.hang.shortlink.admin.utils;

/**
  @author zhaohang1216
  @createTime 2026--04
  @description 密码加密工具类
*/

import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Component;


@Component
public class PasswordEncoderUtil {

    // 加密密码（Hutool一行加密）
    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword);
    }

    // 验证密码（Hutool一行校验）
    public static boolean matches(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}
