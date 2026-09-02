package com.velstrong.bookstore.infrastructure.adapter.out.cache;

import com.velstrong.bookstore.domain.port.out.IamTokenRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * Redis-backed implementation of the IAM token port.
 * Stores access/refresh tokens keyed by user id and a denylist
 * of revoked access tokens (F15 logout).
 */
@Component
public class RedisTokenAdapter implements IamTokenRepository {

    private static final String ACCESS_KEY = "iam:access:";
    private static final String REFRESH_KEY = "iam:refresh:";
    private static final String BLACKLIST_KEY = "iam:blacklist:";

    private final StringRedisTemplate redis;

    public RedisTokenAdapter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void saveAccessToken(String userId, String token, long expirySeconds) {
        redis.opsForValue().set(ACCESS_KEY + userId, token, Duration.ofSeconds(expirySeconds));
    }

    @Override
    public void saveRefreshToken(String userId, String token, long expirySeconds) {
        redis.opsForValue().set(REFRESH_KEY + userId, token, Duration.ofSeconds(expirySeconds));
    }

    @Override
    public Optional<String> findAccessToken(String userId) {
        return Optional.ofNullable(redis.opsForValue().get(ACCESS_KEY + userId));
    }

    @Override
    public Optional<String> findRefreshToken(String userId) {
        return Optional.ofNullable(redis.opsForValue().get(REFRESH_KEY + userId));
    }

    @Override
    public void deleteAccessToken(String userId) {
        redis.delete(ACCESS_KEY + userId);
    }

    @Override
    public void deleteRefreshToken(String userId) {
        redis.delete(REFRESH_KEY + userId);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redis.hasKey(BLACKLIST_KEY + token));
    }

    @Override
    public void blacklistAccessToken(String token, long expirySeconds) {
        redis.opsForValue().set(BLACKLIST_KEY + token, "1", Duration.ofSeconds(expirySeconds));
    }
}
