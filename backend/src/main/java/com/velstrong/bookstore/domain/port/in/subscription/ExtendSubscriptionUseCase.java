package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.command.subscription.ExtendSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;

public interface ExtendSubscriptionUseCase {
    CustomerSubscriptionResponse extend(ExtendSubscriptionCommand command);
}
