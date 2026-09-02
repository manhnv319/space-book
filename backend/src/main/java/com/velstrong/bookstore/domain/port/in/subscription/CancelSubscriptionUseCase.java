package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.command.subscription.CancelSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;

public interface CancelSubscriptionUseCase {
    CustomerSubscriptionResponse cancel(CancelSubscriptionCommand command);
}
