package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UserNotification;

import java.util.Optional;

public interface NotificationRepository {
    UserNotification save(UserNotification notification);
    Optional<UserNotification> findByIdAndUserId(Long notificationId, Long userId);
    PageResult<UserNotification> findByUserId(Long userId, int page, int size);
    long countUnreadByUserId(Long userId);
    void markAllReadByUserId(Long userId);
}
