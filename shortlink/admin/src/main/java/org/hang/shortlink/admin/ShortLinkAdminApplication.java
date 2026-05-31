package org.hang.shortlink.admin;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("org.hang.shortlink.admin.mapper")
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkAdminApplication.class, args);
//        Environment env = context.getEnvironment();
//        String port = env.getProperty("server.port", "8001");
//
//        log.info("\n----------------------------------------------------------\n\t" +
//                "Application Admin is running! Access URLs:\n\t" +
//                "Local Knife4j Doc: \thttp://localhost:{}/doc.html\n\t" +
//                "Swagger UI: \thttp://localhost:{}/swagger-ui\n\t" +
//                "----------------------------------------------------------", port, port);
    }
}
