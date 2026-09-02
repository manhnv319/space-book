package com.velstrong.bookstore.domain.port.in.payment;

public interface GetBankTransferQrUseCase {
    /** PNG of the transfer QR for the caller's own order. */
    byte[] renderQr(Long orderId, Long userId, int size);
}
