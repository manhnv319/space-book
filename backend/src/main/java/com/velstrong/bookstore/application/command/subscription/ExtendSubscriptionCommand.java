package com.velstrong.bookstore.application.command.subscription;

public record ExtendSubscriptionCommand(Long customerSubscriptionId, Long userId, Integer additionalDays) {}
