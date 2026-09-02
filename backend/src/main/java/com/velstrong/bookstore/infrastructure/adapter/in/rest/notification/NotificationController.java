package com.velstrong.bookstore.infrastructure.adapter.in.rest.notification;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.notification.NotificationResponse;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import com.velstrong.bookstore.infrastructure.adapter.out.realtime.UserNotificationEventHub;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    public record PushSubscriptionRequest(String endpoint, Keys keys) { }
    public record Keys(String p256dh, String auth) { }
    private final NotificationUseCase notifications;
    private final UserNotificationEventHub eventHub;
    public NotificationController(NotificationUseCase notifications, UserNotificationEventHub eventHub) { this.notifications = notifications; this.eventHub = eventHub; }

    @GetMapping public ResponseEntity<ApiResponse<PagedResponse<NotificationResponse>>> mine(@RequestAttribute Long currentUserId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(notifications.mine(currentUserId, page, size)));
    }
    @GetMapping("/unread-count") public ResponseEntity<ApiResponse<Long>> unreadCount(@RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(notifications.unreadCount(currentUserId)));
    }
    @PatchMapping("/{id}/read") public ResponseEntity<ApiResponse<NotificationResponse>> markRead(@RequestAttribute Long currentUserId, @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(notifications.markRead(currentUserId, id)));
    }
    @PatchMapping("/read-all") public ResponseEntity<ApiResponse<Void>> markAllRead(@RequestAttribute Long currentUserId) {
        notifications.markAllRead(currentUserId); return ResponseEntity.ok(ApiResponse.success(null));
    }
    @GetMapping("/push/public-key") public ResponseEntity<ApiResponse<String>> publicKey() { return ResponseEntity.ok(ApiResponse.success(notifications.publicPushKey())); }
    @PostMapping("/push/subscriptions") public ResponseEntity<ApiResponse<Void>> subscribe(@RequestAttribute Long currentUserId, @RequestBody PushSubscriptionRequest request) {
        notifications.subscribe(currentUserId, request.endpoint(), request.keys() == null ? null : request.keys().p256dh(), request.keys() == null ? null : request.keys().auth());
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    @DeleteMapping("/push/subscriptions") public ResponseEntity<ApiResponse<Void>> unsubscribe(@RequestAttribute Long currentUserId, @RequestBody PushSubscriptionRequest request) {
        notifications.unsubscribe(currentUserId, request.endpoint()); return ResponseEntity.ok(ApiResponse.success(null));
    }
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE) public SseEmitter stream(@RequestAttribute Long currentUserId) {
        return eventHub.connect(currentUserId);
    }
}
