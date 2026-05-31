package org.hang.shortlink.admin.utils;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

/**
 * 有序UUID v7 生成工具类（RFC 9562标准）
 * 完美适配varchar(32)字段：去横杠后固定32位十六进制字符
 * 特性：时间有序、全局唯一、无第三方依赖、线程安全、分布式开箱即用
 */
@Component
public class UUidV7Generator {
    // 全局单例
    public static final UUidV7Generator INSTANCE = new UUidV7Generator();

    // 安全随机数生成器
    private final SecureRandom secureRandom = new SecureRandom();
    // 48位时间戳掩码
    private static final long TIMESTAMP_MASK = 0xFFFFFFFFFFFFL;
    // UUIDv7版本号固定为 0111 (0x7)
    private static final long VERSION = 0x7000L;
    // RFC4122标准变体标识
    private static final long VARIANT = 0x8000000000000000L;
    // 12位序列掩码
    private static final long RAND_A_MASK = 0xFFFL;

    // 上一次生成ID的时间戳
    private long lastTimestamp = -1L;
    // 同一毫秒内的序列计数器
    private long sequence = 0L;

    // 私有构造，单例模式
    private UUidV7Generator() {}

    /**
     * 生成标准UUID v7字符串（带横杠，36位）
     */
    public synchronized String generate() {
        long currentTimestamp = getCurrentTimestamp();

        // 处理时钟回拨
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，无法生成UUID v7，回拨时长: " + (lastTimestamp - currentTimestamp) + "ms");
        }

        // 同一毫秒内序列递增
        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & RAND_A_MASK;
            // 序列溢出，等待下一毫秒
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 新毫秒重置序列为随机起始值
            sequence = secureRandom.nextLong() & RAND_A_MASK;
        }

        lastTimestamp = currentTimestamp;

        // 构造高64位：48位时间戳 + 4位版本号 + 12位序列
        long mostSigBits = (currentTimestamp & TIMESTAMP_MASK) << 16;
        mostSigBits |= VERSION;
        mostSigBits |= sequence;

        // 构造低64位：2位变体 + 62位随机数
        long leastSigBits = VARIANT | (secureRandom.nextLong() >>> 2);

        return new UUID(mostSigBits, leastSigBits).toString();
    }

    /**
     * 推荐用于生成gid：生成32位无横杠的UUID v7，适配varchar(32)字段
     */
    public String generate32BitGid() {
        return generate().replace("-", "");
    }

    // 获取当前UTC毫秒级时间戳
    private long getCurrentTimestamp() {
        return Instant.now().toEpochMilli();
    }

    // 等待下一毫秒，避免序列溢出
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    // 测试示例
    public static void main(String[] args) {
        // 生成gid
        String gid = UUidV7Generator.INSTANCE.generate32BitGid();
        System.out.println("生成的32位gid: " + gid);
        System.out.println("gid长度: " + gid.length());

        // 批量生成测试
        for (int i = 0; i < 10; i++) {
            System.out.println(UUidV7Generator.INSTANCE.generate32BitGid());
        }
    }

}