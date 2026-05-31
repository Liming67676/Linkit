package org.hang.shortlink.common.autoconfigure.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description redis 配置
 */
@Configuration
@ConditionalOnClass({RedissonClient.class, Redisson.class})
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisConfig {

    /**
     * redisson 连接配置
     */
    @Bean
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties redisProperties) {


//        log.info("DEBUG >>> Redis Host: " + redisProperties.getHost());
//        log.info("DEBUG >>> Redis Password: " + redisProperties.getPassword());
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String username = redisProperties.getUsername();
        String password = redisProperties.getPassword();
        int database = redisProperties.getDatabase();

        Config config = new Config();
        // 绑定 Spring Redis 配置
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setUsername(username)
                .setPassword(password)
                .setDatabase(database);
        return Redisson.create(config);
    }
}
