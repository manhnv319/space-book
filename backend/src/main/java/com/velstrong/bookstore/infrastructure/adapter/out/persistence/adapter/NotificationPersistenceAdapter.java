package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.out.NotificationRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.UserNotificationJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaUserNotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class NotificationPersistenceAdapter implements NotificationRepository {
    private final JpaUserNotificationRepository notifications;

    public NotificationPersistenceAdapter(JpaUserNotificationRepository notifications) { this.notifications = notifications; }

    @Override public UserNotification save(UserNotification value) { return toDomain(notifications.save(toEntity(value))); }
    @Override public Optional<UserNotification> findByIdAndUserId(Long id, Long userId) { return notifications.findByIdAndUserId(id, userId).map(this::toDomain); }
    @Override public PageResult<UserNotification> findByUserId(Long userId, int page, int size) {
        var found = notifications.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size));
        return new PageResult<>(found.getContent().stream().map(this::toDomain).toList(), found.getTotalElements());
    }
    @Override public long countUnreadByUserId(Long userId) { return notifications.countByUserIdAndReadAtIsNull(userId); }
    @Override public void markAllReadByUserId(Long userId) {
        notifications.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, 10_000)).forEach(value -> {
            if (value.getReadAt() == null) { value.setReadAt(LocalDateTime.now()); }
        });
    }

    private UserNotification toDomain(UserNotificationJpaEntity value) {
        return new UserNotification(value.getId(), value.getUserId(), NotificationType.valueOf(value.getType()), value.getTitle(), value.getBody(), value.getTargetPath(), value.getReadAt(), value.getCreatedAt());
    }
    private UserNotificationJpaEntity toEntity(UserNotification value) {
        UserNotificationJpaEntity entity = new UserNotificationJpaEntity();
        entity.setId(value.id()); entity.setUserId(value.userId()); entity.setType(value.type().name()); entity.setTitle(value.title());
        entity.setBody(value.body()); entity.setTargetPath(value.targetPath()); entity.setReadAt(value.readAt()); entity.setCreatedAt(value.createdAt());
        return entity;
    }
}
