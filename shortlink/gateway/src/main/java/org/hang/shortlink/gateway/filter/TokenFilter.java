package org.hang.shortlink.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.constant.TokenConstant;
import org.hang.shortlink.common.utils.JwtUtils;
import org.hang.shortlink.gateway.config.PathPatternProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@EnableConfigurationProperties(PathPatternProperties.class)
@RequiredArgsConstructor
public class TokenFilter implements GlobalFilter, Ordered {

    private static final String AUTH_FAIL_CODE = "A0040";

    private final JwtUtils jwtUtils;
    private final PathPatternProperties pathPatternProperties;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 1. 放行白名单
        if (isExcludePath(path)) {
            return chain.filter(exchange);
        }

        // 2. 提取 token
        List<String> authorization = request.getHeaders().get(jwtUtils.getTokenName());
        if (authorization == null || authorization.isEmpty()) {
            return unauthorizedResponse(exchange, "缺少登录令牌");
        }
        String token = authorization.get(0);

        // 3. JWT 解析（验证签名 + 过期）
        String userInfo;
        try {
            Claims claims = jwtUtils.parseToken(token);
            userInfo = claims.get("username").toString();
        } catch (Exception e) {
            return unauthorizedResponse(exchange, "登录凭证无效或已过期");
        }

        log.info("user_info = {}", userInfo);

        // 4. 校验 Redis 中 token 是否存在（用户是否已登出 / 被踢）
        String redisKey = String.format(TokenConstant.LOGIN_TOKEN_PREFIX, token);
        String finalUserInfo = userInfo;
        return reactiveStringRedisTemplate.hasKey(redisKey)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists)) {
                        return unauthorizedResponse(exchange, "登录已过期，请重新登录");
                    }
                    // 5. Redis 中存在 → 正常放行
                    ServerWebExchange webExchange = exchange.mutate()
                            .request(builder -> builder.header("user_info", finalUserInfo))
                            .build();
                    // redis 续期
                    reactiveStringRedisTemplate.expire(redisKey, Duration.ofMillis(jwtUtils.getTtl()));
                    return chain.filter(webExchange);
                });
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private Boolean isExcludePath(String path) {
        List<String> paths = pathPatternProperties.getExcludePath();
        for (String p : paths) {
            if (antPathMatcher.match(p, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", AUTH_FAIL_CODE);
        body.put("message", message);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsString(body).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":\"" + AUTH_FAIL_CODE + "\",\"message\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        }
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
