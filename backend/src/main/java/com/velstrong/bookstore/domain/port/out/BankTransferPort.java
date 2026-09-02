package com.velstrong.bookstore.domain.port.out;

public interface BankTransferPort {
    String createQrPayload(String bankBin, String accountNumber, long amount, String reference);
}
