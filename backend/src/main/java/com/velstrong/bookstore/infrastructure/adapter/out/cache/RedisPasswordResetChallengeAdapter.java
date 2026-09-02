package com.velstrong.bookstore.infrastructure.adapter.out.cache;

import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class RedisPasswordResetChallengeAdapter implements PasswordResetChallengeRepository {
    private static final DefaultRedisScript<Long> RATE_SCRIPT = new DefaultRedisScript<>(
            "local n = redis.call('INCR', KEYS[1]); "
                    + "if n == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]); end; return n", Long.class);
    private static final DefaultRedisScript<Long> CONSUME_SCRIPT = new DefaultRedisScript<>(
            "local raw = redis.call('GET', KEYS[1]); "
                    + "if not raw then return -1; end; "
                    + "local first = string.find(raw, '|'); local second = string.find(raw, '|', first + 1); "
                    + "local hash = string.sub(raw, 1, first - 1); local uid = string.sub(raw, first + 1, second - 1); "
                    + "local attempts = tonumber(string.sub(raw, second + 1)); "
                    + "if hash == ARGV[1] then redis.call('DEL', KEYS[1]); return tonumber(uid); end; "
                    + "attempts = attempts + 1; if attempts >= tonumber(ARGV[2]) then redis.call('DEL', KEYS[1]); return -3; end; "
                    + "local ttl = redis.call('TTL', KEYS[1]); redis.call('SET', KEYS[1], hash .. '|' .. uid .. '|' .. attempts, 'EX', ttl); return -2",
            Long.class);

    private final StringRedisTemplate redis;

    public RedisPasswordResetChallengeAdapter(StringRedisTemplate redis) { this.redis = redis; }

    @Override
    public boolean allowRequest(String emailHash, Duration ttl, int limit) {
        Long count = redis.execute(RATE_SCRIPT, List.of(emailHash), String.valueOf(ttl.toSeconds()));
        return count != null && count <= limit;
    }

    @Override
    public void put(String challengeId, String otpHash, Long userId, Duration ttl) {
        redis.opsForValue().set(challengeId, otpHash + "|" + userId + "|0", ttl);
    }

    @Override
    public ConsumeResult consume(String challengeId, String otpHash, int maxAttempts) {
        Long result = redis.execute(CONSUME_SCRIPT, List.of(challengeId), otpHash, String.valueOf(maxAttempts));
        if (result == null || result == -1) return new ConsumeResult(ConsumeResult.Status.EXPIRED, null);
        if (result == -2) return new ConsumeResult(ConsumeResult.Status.INVALID, null);
        if (result == -3) return new ConsumeResult(ConsumeResult.Status.EXHAUSTED, null);
        return new ConsumeResult(ConsumeResult.Status.CONSUMED, result);
    }
}
