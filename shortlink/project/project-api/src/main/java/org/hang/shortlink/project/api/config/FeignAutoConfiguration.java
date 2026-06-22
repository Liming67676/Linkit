package org.hang.shortlink.project.api.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Feign 自动配置类
 * 引用此 API 模块的服务会自动启用 Feign 功能
 */
@Configuration
@EnableFeignClients(basePackages = "org.hang.shortlink.project.api.client", // 指定扫描 Feign Client 的包路径
        defaultConfiguration = FeignDefaultConfig.class) // feign 配置（拦截器）
public class FeignAutoConfiguration {
    // 空配置类，仅用于 Feign 被扫描
}