package com.velstrong.bookstore.infrastructure.adapter.out.cache;

import com.velstrong.bookstore.domain.port.out.CacheRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis-backed cache adapter. Used for short-lived items such as
 * password-reset tokens (F15) — values are stored as their {@code toString()}
 * representation and parsed back with the target type's {@code valueOf} or
 * single-arg constructor, avoiding a Jackson dependency at this layer.
 */
@Component
public class RedisCacheAdapter implements CacheRepository {

    private final StringRedisTemplate redis;

    public RedisCacheAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void put(String key, Object value, Duration ttl) {
        redis.opsForValue().set(key, String.valueOf(value), ttl);
    }

    @Override
    public <T> Optional<T> get(String key, Class<T> type) {
        String raw = redis.opsForValue().get(key);
        if (raw == null) return Optional.empty();
        try {
            if (type == String.class) {
                return Optional.of(type.cast(raw));
            }
            if (type == Long.class) {
                return Optional.of(type.cast(Long.valueOf(raw)));
            }
            if (type == Integer.class) {
                return Optional.of(type.cast(Integer.valueOf(raw)));
            }
            if (type == Boolean.class) {
                return Optional.of(type.cast(Boolean.valueOf(raw)));
            }
            return Optional.of(type.cast(raw));
        } catch (ClassCastException | NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(String key) {
        redis.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redis.hasKey(key));
    }
}
