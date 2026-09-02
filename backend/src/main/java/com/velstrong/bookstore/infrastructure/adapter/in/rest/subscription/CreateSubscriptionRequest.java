package com.velstrong.bookstore.infrastructure.adapter.in.rest.subscription;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubscriptionRequest(
        @NotBlank String name,
        String description,
        @NotNull @Positive Long price,
        @NotNull @Positive Integer durationDays,
        @NotNull @Positive Integer maxRentals
) {}
