package com.velstrong.bookstore.infrastructure.adapter.out.push;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.velstrong.bookstore.domain.model.PushSubscription;
import com.velstrong.bookstore.domain.model.UserNotification;
import com.velstrong.bookstore.domain.port.out.PushNotificationSender;
import com.velstrong.bookstore.infrastructure.config.WebPushProperties;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebPushNotificationAdapter implements PushNotificationSender {
    private static final Logger log = LoggerFactory.getLogger(WebPushNotificationAdapter.class);
    private final WebPushProperties properties;
    private final ObjectMapper mapper;
    public WebPushNotificationAdapter(WebPushProperties properties) { this.properties = properties; this.mapper = new ObjectMapper(); }

    @Override public void send(PushSubscription subscription, UserNotification notification) {
        if (!properties.enabled() || properties.publicKey() == null || properties.privateKey() == null || properties.subject() == null) return;
        try {
            String payload = mapper.writeValueAsString(Map.of("title", notification.title(), "body", notification.body(), "targetPath", notification.targetPath(), "type", notification.type().name()));
            PushService service = new PushService(properties.publicKey(), properties.privateKey(), properties.subject());
            service.send(new Notification(subscription.endpoint(), subscription.p256dh(), subscription.auth(), payload));
        } catch (Exception exception) {
            log.warn("Web Push delivery failed for subscription {}: {}", subscription.id(), exception.getClass().getSimpleName());
        }
    }
}
