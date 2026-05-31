package org.hang.shortlink.common.utils;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description ThreadLocal 工具类
 */
public class UserContext {


    private static final ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();
    public static void setUserInfo(String username){
        THREAD_LOCAL.set(username);
    }

    public static String getUserInfo(){
        return THREAD_LOCAL.get();
    }

    public static void clean(){
        THREAD_LOCAL.remove();
    }

}
