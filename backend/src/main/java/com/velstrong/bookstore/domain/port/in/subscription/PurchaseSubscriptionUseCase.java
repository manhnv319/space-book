package com.velstrong.bookstore.domain.port.in.subscription;

import com.velstrong.bookstore.application.command.subscription.PurchaseSubscriptionCommand;
import com.velstrong.bookstore.application.response.subscription.CustomerSubscriptionResponse;

public interface PurchaseSubscriptionUseCase {
    CustomerSubscriptionResponse purchase(PurchaseSubscriptionCommand command);
}
