package org.hang.shortlink.project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * HTTP请求获取客户端操作系统工具类
 */
public class OsUtils {

    // 未知操作系统
    private static final String UNKNOWN_OS = "未知操作系统";

    /**
     * 从请求中获取客户端操作系统
     */
    public static String getOsName(HttpServletRequest request) {
        String clientOs = request.getHeader("Client-OS");
        if (clientOs != null && !clientOs.isEmpty()) {
            return clientOs;
        }
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isEmpty()) {
            return UNKNOWN_OS;
        }
        userAgent = userAgent.toLowerCase();

        // 匹配顺序：移动设备 > 桌面设备
        if (userAgent.contains("windows")) {
            return getWindowsOs(userAgent);
        } else if (userAgent.contains("mac")) {
            // 区分 Mac 电脑 和 iPhone/iPad
            if (userAgent.contains("iphone")) {
                return "iOS (iPhone)";
            } else if (userAgent.contains("ipad")) {
                return "iOS (iPad)";
            }
            return "macOS";
        } else if (userAgent.contains("android")) {
            return "Android";
        } else if (userAgent.contains("linux")) {
            return "Linux";
        } else if (userAgent.contains("unix")) {
            return "Unix";
        }
        return UNKNOWN_OS;
    }

    /**
     * 精细化解析Windows版本
     */
    private static String getWindowsOs(String userAgent) {
        if (userAgent.contains("windows 11")) {
            return "Windows 11";
        }
        if (userAgent.contains("windows nt 10.0")) {
            return "Windows 10";
        } else if (userAgent.contains("windows nt 6.3")) {
            return "Windows 8.1";
        } else if (userAgent.contains("windows nt 6.2")) {
            return "Windows 8";
        } else if (userAgent.contains("windows nt 6.1")) {
            return "Windows 7";
        }
        return "Windows (未知版本)";
    }
}