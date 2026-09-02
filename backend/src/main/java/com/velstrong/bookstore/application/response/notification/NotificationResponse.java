package com.velstrong.bookstore.application.response.notification;

import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, NotificationType type, String title, String body, String targetPath,
                                   LocalDateTime readAt, LocalDateTime createdAt) {
    public static NotificationResponse from(UserNotification value) {
        return new NotificationResponse(value.id(), value.type(), value.title(), value.body(), value.targetPath(), value.readAt(), value.createdAt());
    }
}
