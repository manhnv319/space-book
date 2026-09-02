package com.velstrong.bookstore.application.command.subscription;

public record CancelSubscriptionCommand(Long customerSubscriptionId, Long userId) {}
