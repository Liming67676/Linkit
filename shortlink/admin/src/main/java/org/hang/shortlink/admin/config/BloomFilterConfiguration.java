package org.hang.shortlink.admin.config;


import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BloomFilterConfiguration {

    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient){
        // redisson获取bloom
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        bloomFilter.tryInit(10000000,0.001);
        return bloomFilter;
    }
}