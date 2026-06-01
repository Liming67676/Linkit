package org.hang.shortlink.common.autoconfigure.mvc.adivce;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.enums.BaseErrorCode;
import org.hang.shortlink.common.exception.AbstractException;
import org.hang.shortlink.common.pojo.Result;
import org.hang.shortlink.common.utils.UserContext;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.Optional;

/**
 * 全局异常处理器
 *
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 拦截参数验证异常
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        String field = Optional.ofNullable(firstFieldError).map(FieldError::getField).orElse(StrUtil.EMPTY);
        Object rejectedValue = Optional.ofNullable(firstFieldError).map(FieldError::getRejectedValue).orElse(null);
        log.warn("validation failed method={}, path={}, query={}, user={}, controller={}, field={}, rejectedValue={}, message={}",
                request.getMethod(), request.getRequestURI(), maskQuery(request.getQueryString()),
                getUser(), getControllerMethod(e.getParameter().getExecutable()), field,
                maskValue(field, rejectedValue), exceptionStr, e);
        return Result.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 拦截应用内抛出的异常
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result abstractException(HttpServletRequest request, AbstractException e) {
        if (e.getCause() == null) {
            log.warn("business exception method={}, path={}, query={}, user={}, code={}, message={}, exceptionType={}",
                    request.getMethod(), request.getRequestURI(), maskQuery(request.getQueryString()),
                    getUser(), e.getErrorCode(), e.getErrorMessage(), e.getClass().getName());
        } else {
            log.error("business exception method={}, path={}, query={}, user={}, code={}, message={}, exceptionType={}",
                    request.getMethod(), request.getRequestURI(), maskQuery(request.getQueryString()),
                    getUser(), e.getErrorCode(), e.getErrorMessage(), e.getClass().getName(), e);
        }
        return Result.failure(e);
    }

    /**
     * 拦截未捕获异常
     */
    @ExceptionHandler(value = Throwable.class)
    public Result defaultErrorHandler(HttpServletRequest request,Throwable throwable) {
        log.error("system exception method={}, url={}, user={}, exceptionType={}",
                request.getMethod(), getUrl(request), getUser(), throwable.getClass().getName(), throwable);
        return Result.failure();
    }



    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + maskQuery(request.getQueryString());
    }

    private String getUser() {
        String username = UserContext.getUserInfo();
        return StringUtils.hasText(username) ? username : "anonymous";
    }

    private String getControllerMethod(Object handler) {
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

    private Object maskValue(String field, Object value) {
        if (value == null || !StringUtils.hasText(field)) {
            return value;
        }
        if (field.toLowerCase().contains("password")
                || field.toLowerCase().contains("token")
                || field.toLowerCase().contains("secret")) {
            return "******";
        }
        return value;
    }
}
