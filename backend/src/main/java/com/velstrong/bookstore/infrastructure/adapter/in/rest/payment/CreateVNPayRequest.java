package com.velstrong.bookstore.infrastructure.adapter.in.rest.payment;

import jakarta.validation.constraints.NotNull;

public record CreateVNPayRequest(@NotNull Long orderId) {}
