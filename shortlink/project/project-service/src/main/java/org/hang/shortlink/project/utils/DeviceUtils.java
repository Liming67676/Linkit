package org.hang.shortlink.project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 设备解析
 */
public class DeviceUtils {
    private static final String UNKNOWN_DEVICE = "未知设备";

    public static String getDeviceType(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) return UNKNOWN_DEVICE;
        ua = ua.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "手机";
        }
        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "平板";
        }
        return "PC";
    }
}