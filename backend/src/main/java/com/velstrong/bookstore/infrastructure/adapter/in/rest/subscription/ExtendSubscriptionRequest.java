package com.velstrong.bookstore.infrastructure.adapter.in.rest.subscription;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ExtendSubscriptionRequest(@NotNull @Min(1) Integer additionalDays) {}
