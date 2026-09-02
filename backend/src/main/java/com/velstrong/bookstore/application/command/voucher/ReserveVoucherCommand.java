package com.velstrong.bookstore.application.command.voucher;

public record ReserveVoucherCommand(Long userId, String voucherCode, Long orderId, Long baseAmount) {}
