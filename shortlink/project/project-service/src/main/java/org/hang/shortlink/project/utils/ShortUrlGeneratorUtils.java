package org.hang.shortlink.project.utils;

import cn.hutool.core.lang.UUID;
import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.hang.shortlink.project.constant.ShorUriConstant;
import org.redisson.api.RBloomFilter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author zhaohang1216
 * @createTime 2026--04
 * @description 6位短链接生成工具类
 */
@Component
@RequiredArgsConstructor
public class ShortUrlGeneratorUtils {
    private final RBloomFilter<String> shortLinkBloomFilter;

    // Base62 字符集：0-9, A-Z, a-z
    private static final char[] CHARS =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final int BASE = CHARS.length;

    // 1. 使用 32 位 MurmurHash 计算哈希值
    public  int hash(String url) {
        // 得到一个 32 位的 long 值
        return Hashing.murmur3_32_fixed().hashString(url, StandardCharsets.UTF_8).asInt();
    }
    // 使用 32 位 MurmurHash 带盐值计算哈希值,重复时使用
    private  int hashWithSalt(String url, String salt) {
        return Hashing.murmur3_32_fixed().hashString(url + salt, StandardCharsets.UTF_8).asInt();
    }

    // 2. 将 10 进制长整型转 Base62
    public  String encodeToBase62(int hash32) {
        long num = Integer.toUnsignedLong(hash32);
        StringBuilder sb = new StringBuilder();
        while (num > 0) {
            int remainder = (int) (num % BASE);
            sb.append(CHARS[remainder]);
            num /= BASE;
        }
        return sb.reverse().toString();
    }

    // 3. 对外统一调用方法
    public  String generateShortCode(String prefix, String url) {

        // 1.第一次生成
        int hash32 = hash(url);
        // 将有符号int 转为 无符号long
        String shortCode = encodeToBase62(hash32);
        // 2.bloomfilter：full_short_link 作为唯一键
        // 原子操作，直接添加（合并判定存在 + 添加）
        if (shortLinkBloomFilter.add(prefix + "/" +shortCode)) {
            return shortCode;
        }
        // 3.如果生成的短码重复，则重新生成
        for(int i = 0; i < 5; i++) {
            hash32 = hashWithSalt(url, UUID.fastUUID().toString());
            shortCode = encodeToBase62(hash32);
            if (shortLinkBloomFilter.add(prefix + "/" +shortCode)) {
                return shortCode;
            }
        }
        throw new RuntimeException("短链接生成失败,请稍后再试");
    }


}
