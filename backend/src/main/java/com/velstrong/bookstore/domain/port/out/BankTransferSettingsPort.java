package com.velstrong.bookstore.domain.port.out;

public interface BankTransferSettingsPort {
    boolean isConfigured();
    /** Tên ngân hàng hiển thị cho khách chuyển khoản thủ công (QR chỉ mang mã BIN). */
    String bankName();
    String bankBin();
    String accountNumber();
    String accountName();
    int expiryMinutes();
}
