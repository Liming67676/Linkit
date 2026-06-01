package org.hang.shortlink.project.constant;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
public interface RedisConstant {

    /**
     * 完整短链接信息key
     */
    String FULL_SHORT_URL_KEY = "short-link:full-short-url:%s";

    /**
     * 分组有效短链接数量key
     */
    String GROUP_SHORT_LINK_COUNT_KEY = "short-link:group-count:%s";

    /**
     * 分组短链接数量缓存过期时间
     */
    int GROUP_SHORT_LINK_COUNT_EXPIRE_SECONDS = 3 * 60;


    // -------------- uv/ uip 统计 --------------

    /**
     * 过期时间
     */
    int EXPIRE_DAY_SECONDS = 60 * 60 * 24;

    /**
     * 小时级UV：uv:短链接:日期:小时
     */
    String UV_HOURLY_KEY = "uv:%s:%s";
    /**
     * 小时级UIP：uip:短链接:日期:小时
     */
    String UIP_HOURLY_KEY = "uip:%s:%s";



    // -------------- 锁key --------------
    /**
     * 短链接访问原始链接锁key
     */
    String LOCK_FULL_SHORT_URL_KEY = "short-link:lock:full-short-url:%s";
}
