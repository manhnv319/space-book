package com.velstrong.bookstore.domain.port.in.notification;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.notification.NotificationResponse;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;

public interface NotificationUseCase {
    void notify(Long userId, NotificationType type, String title, String body, String targetPath);
    PagedResponse<NotificationResponse> mine(Long userId, int page, int size);
    long unreadCount(Long userId);
    NotificationResponse markRead(Long userId, Long notificationId);
    void markAllRead(Long userId);
    void subscribe(Long userId, String endpoint, String p256dh, String auth);
    void unsubscribe(Long userId, String endpoint);
    String publicPushKey();
}
