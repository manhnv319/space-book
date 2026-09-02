package com.velstrong.bookstore.application.service.notification;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.notification.NotificationResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.model.PushSubscription;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;
import com.velstrong.bookstore.domain.port.out.NotificationRepository;
import com.velstrong.bookstore.domain.port.out.NotificationEventPublisher;
import com.velstrong.bookstore.domain.port.out.PushNotificationSender;
import com.velstrong.bookstore.domain.port.out.PushSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.PushConfiguration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Transactional
public class NotificationService implements NotificationUseCase {
    private final NotificationRepository notifications;
    private final NotificationEventPublisher eventPublisher;
    private final PushSubscriptionRepository subscriptions;
    private final PushNotificationSender pushSender;
    private final PushConfiguration pushConfiguration;

    public NotificationService(NotificationRepository notifications, NotificationEventPublisher eventPublisher,
                               PushSubscriptionRepository subscriptions, PushNotificationSender pushSender, PushConfiguration pushConfiguration) {
        this.notifications = notifications;
        this.eventPublisher = eventPublisher;
        this.subscriptions = subscriptions;
        this.pushSender = pushSender;
        this.pushConfiguration = pushConfiguration;
    }

    @Override public void notify(Long userId, NotificationType type, String title, String body, String targetPath) {
        if (userId == null) return;
        UserNotification saved = notifications.save(UserNotification.create(userId, type, title, body, safeTarget(targetPath)));
        eventPublisher.publish(userId, saved, notifications.countUnreadByUserId(userId));
        subscriptions.findByUserId(userId).forEach(subscription -> pushSender.send(subscription, saved));
    }

    @Override @Transactional(readOnly = true)
    public PagedResponse<NotificationResponse> mine(Long userId, int page, int size) {
        var found = notifications.findByUserId(userId, Math.max(0, page), Math.clamp(size, 1, 50));
        return PagedResponse.of(found.content().stream().map(NotificationResponse::from).toList(), page, size, found.totalElements());
    }

    @Override @Transactional(readOnly = true) public long unreadCount(Long userId) { return notifications.countUnreadByUserId(userId); }

    @Override public NotificationResponse markRead(Long userId, Long notificationId) {
        UserNotification found = notifications.findByIdAndUserId(notificationId, userId).orElseThrow(() -> new EntityNotFoundException("Notification", notificationId));
        UserNotification saved = notifications.save(found.markRead());
        eventPublisher.publish(userId, saved, notifications.countUnreadByUserId(userId));
        return NotificationResponse.from(saved);
    }

    @Override public void markAllRead(Long userId) {
        notifications.markAllReadByUserId(userId);
        eventPublisher.publish(userId, null, 0);
    }

    @Override public void subscribe(Long userId, String endpoint, String p256dh, String auth) {
        if (endpoint == null || endpoint.length() > 2000 || p256dh == null || p256dh.length() > 200 || auth == null || auth.length() > 200) throw new IllegalArgumentException("Đăng ký thiết bị không hợp lệ.");
        PushSubscription value = subscriptions.findByUserIdAndEndpoint(userId, endpoint)
                .map(current -> new PushSubscription(current.id(), userId, endpoint, p256dh, auth, current.createdAt()))
                .orElseGet(() -> PushSubscription.create(userId, endpoint, p256dh, auth));
        subscriptions.save(value);
    }

    @Override public void unsubscribe(Long userId, String endpoint) { if (endpoint != null) subscriptions.deleteByUserIdAndEndpoint(userId, endpoint); }
    @Override public String publicPushKey() { return pushConfiguration.publicKey(); }

    private String safeTarget(String targetPath) {
        return targetPath != null && targetPath.startsWith("/") && !targetPath.startsWith("//") ? targetPath : "/";
    }
}
