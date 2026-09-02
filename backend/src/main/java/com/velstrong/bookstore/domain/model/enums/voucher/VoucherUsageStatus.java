package com.velstrong.bookstore.domain.model.enums.voucher;

public enum VoucherUsageStatus {
    RESERVED,
    COMMITTED,
    CANCELLED,
    EXPIRED;

    public boolean isReserved() { return this == RESERVED; }
    public boolean isCommitted() { return this == COMMITTED; }
}
