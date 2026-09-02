package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PushSubscription;
import com.velstrong.bookstore.domain.model.UserNotification;

public interface PushNotificationSender {
    void send(PushSubscription subscription, UserNotification notification);
}
