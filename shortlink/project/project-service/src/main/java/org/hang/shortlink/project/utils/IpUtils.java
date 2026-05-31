package org.hang.shortlink.project.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.hang.shortlink.project.pojo.dto.GaoDeIpLocationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * IP工具类： 获取客户端真实IP
 */
@Component
@EnableConfigurationProperties(GaoDeProperties.class)
public class IpUtils {
    // 未知IP常量
    private  final String UNKNOWN = "unknown";
    // 本地IP
    private  final String LOCAL_IP = "127.0.0.1";
    // IPv6本地地址
    private  final String LOCAL_IPV6 = "0:0:0:0:0:0:0:1";


    @Autowired
    private GaoDeProperties gaoDeProperties;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 获取客户端真实IP
     * X-Forwarded-For 具有欺骗性，安全性较低
     */
    public  String getClientIp(HttpServletRequest request) {
        // 1. 从代理请求头中获取IP（优先级从高到低）
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        // 2. 无代理时，直接获取远程IP
        if (ip == null || ip.isEmpty() || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 3. 处理多层代理：X-Forwarded-For 格式为 客户端IP,代理1IP,代理2IP... 取第一个非unknown的IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        // 4. 本地IPv6转换为IPv4
        if (LOCAL_IPV6.equals(ip)) {
            ip = LOCAL_IP;
        }
        return ip;
    }

    /**
     * 根据IP获取地理位置信息（目前仅支持国内ip）
     * @param ip 客户端IP
     * @return 省份、城市、城市编码
     */
    public GaoDeIpLocationDTO getLocationByIp(String ip) {
        // 1. 校验IP和Key
        if (ip == null || ip.isBlank() || gaoDeProperties.getAccessKey() == null) {
            return null;
        }
        // 本地IP直接返回空（无需调用高德）
        if ("127.0.0.1".equals(ip)) {
            return null;
        }

        try {
            // 2. 拼接请求URL
            String url = String.format(
                    gaoDeProperties.getApiUrl(),
                    ip,
                    gaoDeProperties.getAccessKey()
            );

            // 3. 调用高德API，解析JSON响应
            GaoDeIpLocationDTO response = restTemplate.getForObject(url, GaoDeIpLocationDTO.class);

            // 4. 校验响应是否成功
            if (response != null && "1".equals(response.getStatus()) && "10000".equals(response.getInfocode())) {
                return response;
            }
        } catch (Exception e) {
            // 调用失败（网络异常/API限流），打印日志，不影响主流程
            e.printStackTrace();
        }
        return null;
    }
}