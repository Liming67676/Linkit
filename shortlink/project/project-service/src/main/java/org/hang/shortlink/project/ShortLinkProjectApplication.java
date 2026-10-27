package org.hang.shortlink.project;


import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 一句话描述作用
 */
@Slf4j
@SpringBootApplication
@MapperScan("org.hang.shortlink.project.mapper")
@EnableScheduling // 开启定时任务
public class ShortLinkProjectApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ShortLinkProjectApplication.class, args);
//        Environment env = context.getEnvironment();
//        String port = env.getProperty("server.port", "8002");
//
//        log.info("\n----------------------------------------------------------\n\t" +
//                "Application Project is running! Access URLs:\n\t" +
//                "Local Knife4j Doc: \thttp://localhost:{}/doc.html\n\t" +
//                "Swagger UI: \thttp://localhost:{}/swagger-ui\n\t" +
//                "----------------------------------------------------------", port, port);
    }
}
