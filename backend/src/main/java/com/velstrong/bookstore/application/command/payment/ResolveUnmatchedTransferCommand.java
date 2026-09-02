package com.velstrong.bookstore.application.command.payment;

public record ResolveUnmatchedTransferCommand(Long transferId, Long orderId) {}
