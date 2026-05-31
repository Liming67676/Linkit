package org.hang.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 短链接bloom 过滤器配置
 */
@Configuration
public class BloomFilterConfig {

    @Bean
    public RBloomFilter<String> shortLinkBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("shortLinkBloomFilter");
        bloomFilter.tryInit(10000000,0.001);
        return bloomFilter;
    }

}
