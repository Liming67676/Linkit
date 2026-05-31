package org.hang.shortlink.project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 浏览器解析
 */
public class BrowserUtils {
    private static final String UNKNOWN_BROWSER = "未知浏览器";

    public static String getBrowserName(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        if (ua == null) return UNKNOWN_BROWSER;
        ua = ua.toLowerCase();
        if (ua.contains("edg")) return "Edge";
        if (ua.contains("chrome")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari")) return "Safari";
        if (ua.contains("opera")) return "Opera";
        if (ua.contains("trident")) return "IE";
        return UNKNOWN_BROWSER;
    }
}