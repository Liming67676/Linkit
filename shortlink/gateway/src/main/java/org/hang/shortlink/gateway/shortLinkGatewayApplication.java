package org.hang.shortlink.gateway;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Slf4j
@SpringBootApplication
public class shortLinkGatewayApplication {
    public static void main(String[] args) {
       SpringApplication.run(shortLinkGatewayApplication.class, args);
    }
}
