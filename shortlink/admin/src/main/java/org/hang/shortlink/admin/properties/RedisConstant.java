package org.hang.shortlink.admin.properties;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface RedisConstant {

    /**
     * 封装创键lock
     */
    String GROUP_ADD_LOCK = "short-link:lock:group-add:%s";
}
