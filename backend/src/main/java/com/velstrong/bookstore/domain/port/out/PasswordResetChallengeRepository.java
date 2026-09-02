package com.velstrong.bookstore.domain.port.out;

import java.time.Duration;

public interface PasswordResetChallengeRepository {
    boolean allowRequest(String emailHash, Duration ttl, int limit);
    void put(String challengeId, String otpHash, Long userId, Duration ttl);
    ConsumeResult consume(String challengeId, String otpHash, int maxAttempts);

    record ConsumeResult(Status status, Long userId) {
        public enum Status { CONSUMED, INVALID, EXPIRED, EXHAUSTED }
    }
}
