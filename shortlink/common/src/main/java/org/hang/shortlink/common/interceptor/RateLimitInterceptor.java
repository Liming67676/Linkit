package org.hang.shortlink.common.interceptor;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.propreties.RateLimitProperties;
import org.hang.shortlink.common.utils.UserContext;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * 风控限流：
 * 创键功能限流 30;
 * 其它限流 300
 */
public class RateLimitInterceptor implements HandlerInterceptor {

    private final RedissonClient redissonClient;
    private final RateLimitProperties rateLimitProperties;
    private final ObjectMapper objectMapper;

    private static final String ACCESS_RATE_KEY_PREFIX = "short-link:rate:access:";
    private static final String CREATE_RATE_KEY_PREFIX = "short-link:rate:create:";

    public RateLimitInterceptor(RedissonClient redissonClient, RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        this.redissonClient = redissonClient;
        this.rateLimitProperties = rateLimitProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String key; // 限流的唯一身份证，用户或者ip
        int qps; // 每分钟最多能发多少次请求

        if (isCreatePath(requestURI)) { // 创键用户限流
            String username = UserContext.getUserInfo();
            key = CREATE_RATE_KEY_PREFIX + (StrUtil.isBlank(username) ? getIpAddress(request) : username);
            qps = rateLimitProperties.getCreateQps();
        } else { // 其它ip限流
            key = ACCESS_RATE_KEY_PREFIX + getIpAddress(request);
            qps = rateLimitProperties.getAccessQps();
        }

        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, qps, 60, RateIntervalUnit.SECONDS);
        // 过期时间设为120秒（2分钟，是限流窗口的2倍）
        rateLimiter.expire(120, TimeUnit.SECONDS);

        if (!rateLimiter.tryAcquire()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            String json = objectMapper.writeValueAsString(Result.failure("请求过于频繁，请稍后再试"));
            response.getWriter().write(json);
            return false;
        }

        return true;
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.contains(",") ? ip.split(",")[0].trim() : ip;
    }

    private boolean isCreatePath(String requestURI) {
        return rateLimitProperties.getCreatePaths().stream().anyMatch(requestURI::startsWith);
    }
}
