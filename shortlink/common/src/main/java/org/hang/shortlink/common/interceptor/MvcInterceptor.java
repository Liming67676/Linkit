package org.hang.shortlink.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.utils.UserContext;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
public class MvcInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME = "shortlink_request_start_time";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userInfo = request.getHeader("user_info");
        if (StringUtils.hasText(userInfo)) {
            UserContext.setUserInfo(userInfo);
        }
        request.setAttribute(REQUEST_START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        UserContext.clean();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startTime = request.getAttribute(REQUEST_START_TIME);
        long costMs = startTime instanceof Long ? System.currentTimeMillis() - (Long) startTime : -1L;
        String userInfo = request.getHeader("user_info");
        String user = StringUtils.hasText(userInfo) ? userInfo : "anonymous";
        String path = request.getRequestURI();
        String query = maskQuery(request.getQueryString());
        String fullPath = StringUtils.hasText(query) ? path + "?" + query : path;
        String handlerName = getHandlerName(handler);
        if (ex != null) {
            log.error("request error handler={}, method={}, path={},  status={}, user={}, costMs={}",
                    handlerName, request.getMethod(), fullPath, response.getStatus(), user, costMs, ex);
            return;
        }
        log.info("handler={},request method={}, path={},  status={}, user={}, costMs={}",
                handlerName, request.getMethod(), fullPath, response.getStatus(), user, costMs);
    }

    private String getHandlerName(Object handler) {
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getBeanType().getName() + "#" + handlerMethod.getMethod().getName();
        }
        return String.valueOf(handler);
    }

    private String maskQuery(String queryString) {
        if (!StringUtils.hasText(queryString)) {
            return "";
        }
        return queryString.replaceAll("(?i)(password|token|secret|key)=([^&]*)", "$1=******");
    }
}
