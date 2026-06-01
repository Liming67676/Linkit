package org.hang.shortlink.common.autoconfigure.mvc;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.hang.shortlink.common.autoconfigure.mvc.adivce.GlobalExceptionAdvice;
import org.hang.shortlink.common.interceptor.GatewayAccessInterceptor;
import org.hang.shortlink.common.interceptor.MvcInterceptor;
import org.hang.shortlink.common.interceptor.RateLimitInterceptor;
import org.hang.shortlink.common.propreties.GatewayAccessProperties;
import org.hang.shortlink.common.propreties.RateLimitProperties;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
@EnableConfigurationProperties({RateLimitProperties.class, GatewayAccessProperties.class})
public class WebMvcConfig {

    @Bean
    public GlobalExceptionAdvice commonExceptionAdvice(){
        return new GlobalExceptionAdvice();
    }

    @Bean
    @ConditionalOnProperty(prefix = "short.link.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = false)
    public RateLimitInterceptor rateLimitInterceptor(RedissonClient redissonClient, RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        return new RateLimitInterceptor(redissonClient, rateLimitProperties, objectMapper);
    }

    @Bean
    @ConditionalOnProperty(prefix = "short.link.gateway-access", name = "enabled", havingValue = "true")
    public GatewayAccessInterceptor gatewayAccessInterceptor(GatewayAccessProperties gatewayAccessProperties, ObjectMapper objectMapper) {
        return new GatewayAccessInterceptor(gatewayAccessProperties, objectMapper);
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurer(ObjectProvider<RateLimitInterceptor> rateLimitProvider,
                                             ObjectProvider<GatewayAccessInterceptor> gatewayAccessProvider) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                GatewayAccessInterceptor gatewayAccessInterceptor = gatewayAccessProvider.getIfAvailable();
                if (gatewayAccessInterceptor != null) {
                    registry.addInterceptor(gatewayAccessInterceptor)
                            .addPathPatterns("/**");
                }
                registry.addInterceptor(new MvcInterceptor())
                        .excludePathPatterns("/page/**/*.html","/*.html");
                RateLimitInterceptor rateLimitInterceptor = rateLimitProvider.getIfAvailable();
                if (rateLimitInterceptor != null) {
                    registry.addInterceptor(rateLimitInterceptor)
                            .addPathPatterns("/api/short-link/admin/v1/**");
                }
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
                registry.addResourceHandler("/doc.html")
                        .addResourceLocations("classpath:/META-INF/resources/");
                registry.addResourceHandler("/webjars/**")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/");
            }
        };
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Sass短链接服务Api接口文档")
                        .description("Sass短链接服务后端接口文档")
                        .contact(new Contact().name("zhaohang1216"))
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org"))
                );

    }
}
