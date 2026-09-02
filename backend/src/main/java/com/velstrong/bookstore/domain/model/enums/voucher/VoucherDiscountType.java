package com.velstrong.bookstore.domain.model.enums.voucher;

public enum VoucherDiscountType {
    PERCENTAGE,
    FIXED_AMOUNT;

    public boolean isPercentage() { return this == PERCENTAGE; }
    public boolean isFixedAmount() { return this == FIXED_AMOUNT; }
}
