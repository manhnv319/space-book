package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;

import java.time.LocalDateTime;

public record UserNotification(Long id, Long userId, NotificationType type, String title, String body,
                               String targetPath, LocalDateTime readAt, LocalDateTime createdAt) {
    public static UserNotification create(Long userId, NotificationType type, String title, String body, String targetPath) {
        return new UserNotification(null, userId, type, title, body, targetPath, null, LocalDateTime.now());
    }

    public UserNotification markRead() {
        return readAt == null ? new UserNotification(id, userId, type, title, body, targetPath, LocalDateTime.now(), createdAt) : this;
    }
}
