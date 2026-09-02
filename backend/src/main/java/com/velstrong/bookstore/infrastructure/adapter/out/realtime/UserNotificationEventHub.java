package com.velstrong.bookstore.infrastructure.adapter.out.realtime;

import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.port.out.NotificationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserNotificationEventHub implements NotificationEventPublisher {
    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000L;
    private final ConcurrentHashMap<Long, Set<SseEmitter>> streams = new ConcurrentHashMap<>();

    public SseEmitter connect(Long userId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT_MILLIS);
        streams.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(emitter);
        Runnable remove = () -> remove(userId, emitter);
        emitter.onCompletion(remove);
        emitter.onTimeout(remove);
        emitter.onError(ignored -> remove.run());
        try { emitter.send(SseEmitter.event().name("ready").data("ok")); }
        catch (IOException exception) { emitter.completeWithError(exception); }
        return emitter;
    }

    @Override
    public void publish(Long userId, UserNotification notification, long unreadCount) {
        streams.getOrDefault(userId, Set.of()).forEach(emitter -> {
            try { emitter.send(SseEmitter.event().name("notification").data(new NotificationEvent(notification, unreadCount))); }
            catch (IOException exception) { remove(userId, emitter); }
        });
    }

    private void remove(Long userId, SseEmitter emitter) {
        streams.computeIfPresent(userId, (ignored, emitters) -> { emitters.remove(emitter); return emitters.isEmpty() ? null : emitters; });
    }

    public record NotificationEvent(UserNotification notification, long unreadCount) { }
}
