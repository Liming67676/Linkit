package org.hang.shortlink.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.enums.BaseErrorCode;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.propreties.GatewayAccessProperties;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class GatewayAccessInterceptor implements HandlerInterceptor {

    private final GatewayAccessProperties gatewayAccessProperties;

    private final ObjectMapper objectMapper;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    @SneakyThrows
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!Boolean.TRUE.equals(gatewayAccessProperties.getEnabled()) || isExcludePath(request.getRequestURI())) {
            return true;
        }
        String gatewayHeader = request.getHeader(gatewayAccessProperties.getHeaderName());
        if (gatewayAccessProperties.getHeaderValue().equals(gatewayHeader)) {
            return true;
        }
        log.warn("blocked direct service request method={}, path={}, remoteAddr={}",
                request.getMethod(), request.getRequestURI(), request.getRemoteAddr());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(
                Result.failure(BaseErrorCode.CLIENT_ERROR.code(), "请通过网关访问服务")));
        return false;
    }

    private boolean isExcludePath(String path) {
        if (!StringUtils.hasText(path)) {
            return false;
        }
        return gatewayAccessProperties.getExcludePath().stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }
}
