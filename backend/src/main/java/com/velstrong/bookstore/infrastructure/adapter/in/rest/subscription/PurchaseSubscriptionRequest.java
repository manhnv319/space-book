package com.velstrong.bookstore.infrastructure.adapter.in.rest.subscription;

import jakarta.validation.constraints.NotNull;

public record PurchaseSubscriptionRequest(@NotNull Long subscriptionId) {}
