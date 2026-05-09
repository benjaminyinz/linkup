package com.linkup.infrastructure.lock;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * Redisson 分布式锁封装。
 *
 * <p>提供两种风格：</p>
 * <ul>
 *   <li>{@link #execute(String, Duration, Duration, Supplier)} —— 自动管理 try/finally，业务无需关心释放</li>
 *   <li>{@link #tryAcquire(String, Duration, Duration)} —— 手动获取 RLock，调用方负责 unlock（特殊场景如跨方法持锁）</li>
 * </ul>
 *
 * <p>Key 命名约定：`linkup:lock:&lt;business&gt;:&lt;id&gt;`，避免与缓存 key 冲突。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DistributedLock {

    private final RedissonClient redissonClient;

    /**
     * 在锁保护下执行业务逻辑；获取锁失败抛 LockAcquireException。
     *
     * @param key       锁 key
     * @param waitTime  最长等待获取锁的时间
     * @param leaseTime 获取后自动释放的时间（防止业务挂死后锁不释放）
     * @param action    业务逻辑
     */
    public <T> T execute(String key, Duration waitTime, Duration leaseTime, Supplier<T> action) {
        RLock lock = redissonClient.getLock(key);
        boolean acquired = false;
        try {
            acquired = lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
            if (!acquired) {
                throw new LockAcquireException("获取分布式锁失败: " + key);
            }
            return action.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquireException("等待分布式锁被中断: " + key, e);
        } finally {
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * 手动获取锁；调用方必须在 finally 中 unlock。
     */
    public RLock tryAcquire(String key, Duration waitTime, Duration leaseTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean ok = lock.tryLock(waitTime.toMillis(), leaseTime.toMillis(), TimeUnit.MILLISECONDS);
            return ok ? lock : null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new LockAcquireException("等待分布式锁被中断: " + key, e);
        }
    }

    public static class LockAcquireException extends RuntimeException {
        public LockAcquireException(String message) {
            super(message);
        }
        public LockAcquireException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
