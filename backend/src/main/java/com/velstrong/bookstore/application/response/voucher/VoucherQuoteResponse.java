package com.velstrong.bookstore.application.response.voucher;

import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;

public record VoucherQuoteResponse(
        boolean valid,
        Long discountAmount,
        Long finalAmount,
        VoucherValidationReason reason
) {
    public static VoucherQuoteResponse valid(Long discountAmount, Long finalAmount) {
        return new VoucherQuoteResponse(true, discountAmount, finalAmount, null);
    }

    public static VoucherQuoteResponse invalid(VoucherValidationReason reason) {
        return new VoucherQuoteResponse(false, 0L, null, reason);
    }
}
