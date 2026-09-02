package com.velstrong.bookstore.infrastructure.adapter.in.rest.voucher;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuoteVoucherRequest(@NotBlank String voucherCode, @NotNull Long baseAmount) {}
