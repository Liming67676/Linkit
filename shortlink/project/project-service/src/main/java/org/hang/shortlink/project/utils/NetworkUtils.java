package org.hang.shortlink.project.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 网络类型解析
 */
public class NetworkUtils {
    private static final String WIFI = "WIFI";

    /**
     * 简易网络类型解析（常规场景可用，小程序/APP可通过请求头传递网络类型）
     */
    public static String getNetworkType(HttpServletRequest request) {
        // 前端/小程序可通过 X-Network-Type 传递：wifi/4g/5g
        String network = request.getHeader("X-Network-Type");
        if (network != null) {
            return network.toUpperCase();
        }
        network = request.getParameter("network");
        if (network != null && !network.trim().isEmpty()) {
            return network.toUpperCase();
        }
        return WIFI;
    }
}