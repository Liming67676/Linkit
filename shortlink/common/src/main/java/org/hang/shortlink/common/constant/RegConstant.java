package org.hang.shortlink.common.constant;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 正则公共类
 */
public interface RegConstant {

    // 11 位手机号
    String phoneReg = "^1[3456789]\\d{9}$";

    // 至少 8 位，且包含大写字母、小写字母、数字和特殊符号
    String passwordReg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";

    // 邮箱
    String mailReg = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
}
