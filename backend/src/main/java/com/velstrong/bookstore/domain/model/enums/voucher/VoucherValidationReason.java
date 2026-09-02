package com.velstrong.bookstore.domain.model.enums.voucher;

public enum VoucherValidationReason {
    NOT_FOUND,
    EXPIRED,
    NOT_YET_ACTIVE,
    USAGE_LIMIT_REACHED,
    USER_LIMIT_REACHED,
    MIN_ORDER_NOT_MET,
    INACTIVE,
    TIER_NOT_ELIGIBLE;
}
