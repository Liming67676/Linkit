package org.hang.shortlink.admin.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hang.shortlink.admin.mapper.UserMapper;
import org.hang.shortlink.admin.pojo.po.User;
import org.hang.shortlink.admin.utils.PasswordEncoderUtil;
import org.redisson.api.RBloomFilter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 预热登录链路依赖，避免首次真实登录承担冷启动成本。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminStartupWarmup {

    private static final String WARMUP_KEY = "shortlink:admin:warmup";

    private static final String WARMUP_PASSWORD_HASH = "$2a$10$/dmo8GPDh8WJlJ/7uIltHOqQjNUg.pTE4p0qPTa4yWpTafCpXfNLG";

    private final UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    private final RBloomFilter<String> bloomFilter;

    @EventListener(ApplicationReadyEvent.class)
    public void warmup() {
        long startTime = System.nanoTime();
        try {
            warmupDatabase();
            warmupRedis();
            warmupBloomFilter();
            warmupPasswordEncoder();
            log.info("admin startup warmup completed costMs={}", elapsedMillis(startTime));
        } catch (Exception ex) {
            log.warn("admin startup warmup failed costMs={}", elapsedMillis(startTime), ex);
        }
    }

    private void warmupDatabase() {
        userMapper.selectList(Wrappers.lambdaQuery(User.class)
                .select(User::getId)
                .eq(User::getUsername, WARMUP_KEY)
                .last("LIMIT 1"));
    }

    private void warmupRedis() {
        stringRedisTemplate.hasKey(WARMUP_KEY);
    }

    private void warmupBloomFilter() {
        bloomFilter.contains(WARMUP_KEY);
    }

    private void warmupPasswordEncoder() {
        PasswordEncoderUtil.matches(WARMUP_KEY, WARMUP_PASSWORD_HASH);
    }

    private long elapsedMillis(long startTime) {
        return java.util.concurrent.TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
    }
}
