package com.linkup.common.util;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * UUID 工具类。
 *
 * <p>数据库表结构里约定主键由应用层生成 UUID v7。
 * UUID v7 的前半部分包含毫秒时间戳，所以大致按时间递增，
 * 比完全随机的 UUID v4 更适合作为数据库主键。</p>
 *
 * <p>这里先实现一个轻量版本，满足 MVP 阶段的主键生成需求。
 * 后续如果要追求严格单调递增，可以再换成专门的 UUID v7 库。</p>
 */
public final class UuidUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private UuidUtil() {
    }

    /**
     * 生成 UUID v7。
     *
     * <p>结构简化理解：</p>
     * <ul>
     *     <li>前 48 位：当前毫秒时间戳。</li>
     *     <li>中间 4 位：版本号 7。</li>
     *     <li>剩余部分：随机数，避免同一毫秒内重复。</li>
     * </ul>
     */
    public static UUID newUuidV7() {
        long timestampMillis = System.currentTimeMillis() & 0xFFFFFFFFFFFFL;
        long randomA = RANDOM.nextLong() & 0x0FFFL;

        long mostSignificantBits = (timestampMillis << 16) | 0x7000L | randomA;

        long leastSignificantBits = RANDOM.nextLong();
        leastSignificantBits = (leastSignificantBits & 0x3FFFFFFFFFFFFFFFL) | 0x8000000000000000L;

        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
