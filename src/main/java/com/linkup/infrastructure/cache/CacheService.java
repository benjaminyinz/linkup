package com.linkup.infrastructure.cache;

import java.time.Duration;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 缓存门面。
 *
 * <p>业务代码不直接依赖 RedisTemplate，统一通过本门面操作；
 * 后期需要切换到本地缓存 / 多级缓存时改这里即可。</p>
 *
 * <p>Key 命名约定（业务层调用时遵守，便于追踪）：
 * <pre>
 * linkup:&lt;domain&gt;:&lt;subject&gt;:&lt;id&gt;
 * 例如：linkup:activity:detail:01HV..., linkup:auth:token-blacklist:&lt;jti&gt;
 * </pre></p>
 */
@Component
@RequiredArgsConstructor
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        return Optional.of(type.cast(value));
    }

    public void put(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public boolean putIfAbsent(String key, Object value, Duration ttl) {
        Boolean ok = redisTemplate.opsForValue().setIfAbsent(key, value, ttl);
        return Boolean.TRUE.equals(ok);
    }

    public void evict(String key) {
        redisTemplate.delete(key);
    }

    public boolean exists(String key) {
        Boolean ok = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(ok);
    }

    /**
     * 缓存穿透保护：先读，命中直接返回；未命中调用 loader 加载并回填。
     * loader 返回 null 时不写缓存，避免缓存空值；如需缓存空值由调用方包装一个空的占位对象。
     */
    public <T> T getOrLoad(String key, Class<T> type, Duration ttl, Supplier<T> loader) {
        return get(key, type).orElseGet(() -> {
            T loaded = loader.get();
            if (loaded != null) {
                put(key, loaded, ttl);
            }
            return loaded;
        });
    }
}
