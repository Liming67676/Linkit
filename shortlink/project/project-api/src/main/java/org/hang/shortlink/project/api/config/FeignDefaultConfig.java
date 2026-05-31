package org.hang.shortlink.project.api.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.common.utils.UserContext;
import org.springframework.context.annotation.Bean;

/**
 * 依据 最小影响原则
 * 该类不用加@Configuration，@EnableFeignClients中有属性可以在使用时注入
 */

//@Slf4j
public class FeignDefaultConfig {

    /**
     * 日志
     * @return
     */
//    @Bean
//    public Logger.Level feignLoggerLevel() {
//        return Logger.Level.FULL;
//    }

    /**
     * Feign拦截器，会自动使用
     * @return
     */
    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 获取登录用户
                String username = UserContext.getUserInfo();
                if(username == null) {
                    // 如果为空则直接跳过
                    return;
                }
                // 如果不为空则放入请求头中，传递给下游微服务
//                log.info("feign 拦截器拦截，用户：{}", username);
                template.header("user_info", username);
            }
        };
    }
}