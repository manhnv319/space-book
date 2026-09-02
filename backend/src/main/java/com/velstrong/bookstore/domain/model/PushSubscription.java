package com.velstrong.bookstore.domain.model;

import java.time.LocalDateTime;

public record PushSubscription(Long id, Long userId, String endpoint, String p256dh, String auth, LocalDateTime createdAt) {
    public static PushSubscription create(Long userId, String endpoint, String p256dh, String auth) {
        return new PushSubscription(null, userId, endpoint, p256dh, auth, LocalDateTime.now());
    }
}
