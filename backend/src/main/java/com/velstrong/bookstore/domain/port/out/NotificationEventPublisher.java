package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.UserNotification;

public interface NotificationEventPublisher {
    void publish(Long userId, UserNotification notification, long unreadCount);
}
