package com.velstrong.bookstore.application.command.payment;

import java.util.Map;

public record ConfirmPaymentCommand(Map<String, String> vnpayParams) {}
