package org.hang.shortlink.project.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Cookie生成工具类，用于识别同一用户
 */
public class CookieUserUtil {
    
    /**
     * 生成用户标识Cookie
     *
     * @param response   HTTP响应对象
     * @param cookieName Cookie名称
     * @param maxAge     Cookie最大存活时间（秒）
     */
    public static String generateUserCookie(HttpServletResponse response, String cookieName, int maxAge) {
        String userIdentifier = generateUniqueIdentifier();
        
        Cookie cookie = new Cookie(cookieName, userIdentifier);
        cookie.setMaxAge(maxAge); // 设置过期时间
        cookie.setPath("/"); // 设置路径，对整个应用有效
        cookie.setHttpOnly(true); // 防止XSS攻击
        cookie.setSecure(false); // 根据需要设置是否只在HTTPS下传输
        response.addCookie(cookie);
        return userIdentifier;
    }
    
    /**
     * 获取用户标识
     * 
     * @param request HTTP请求对象
     * @param cookieName Cookie名称
     * @return 用户标识，如果不存在则返回null
     */
    public static String getUserIdentifier(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    /**
     * 更新Cookie的过期时间
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param cookieName Cookie名称
     * @param maxAge 新的过期时间（秒）
     */
    public static void updateCookieExpiration(HttpServletRequest request, 
                                            HttpServletResponse response, 
                                            String cookieName, 
                                            int maxAge) {
        String currentIdentifier = getUserIdentifier(request, cookieName);
        if (currentIdentifier != null) {
            // 重新生成并设置Cookie，更新过期时间
            generateUserCookie(response, cookieName, maxAge);
        }
    }
    
    /**
     * 清除用户标识Cookie
     * 
     * @param response HTTP响应对象
     * @param cookieName Cookie名称
     */
    public static void clearUserCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0); // 立即过期
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    /**
     * 检查用户是否存在
     *
     * @param request HTTP请求对象
     * @param cookieName Cookie名称
     * @return 如果用户存在返回true，否则返回false
     */
    public static boolean isUserExist(HttpServletRequest request, String cookieName) {
        return getUserIdentifier(request, cookieName) != null;
    }

    /**
     * 生成唯一的用户标识
     *
     * @return 唯一的用户标识字符串
     */
    private static String generateUniqueIdentifier() {
        try {
            // 结合UUID、时间戳和随机数生成更安全的标识
            String baseString = UUID.randomUUID().toString() +
                    System.currentTimeMillis() +
                    Math.random();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(baseString.getBytes());

            // Base64编码，去除特殊字符
            return Base64.getEncoder()
                    .encodeToString(hashBytes)
                    .replace("+", "")
                    .replace("/", "")
                    .replace("=", "")
                    .substring(0, 32); // 截取前32位
        } catch (NoSuchAlgorithmException e) {
            // 如果SHA-256不可用，回退到UUID
            return UUID.randomUUID().toString().replace("-", "");
        }
    }
}