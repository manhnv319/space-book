package com.velstrong.bookstore.application.command.voucher;

public record QuoteVoucherCommand(Long userId, String voucherCode, Long baseAmount) {}
