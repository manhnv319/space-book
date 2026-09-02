package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.command.payment.BankTransferNotification;

public interface ConfirmBankTransferUseCase {
    void confirm(BankTransferNotification notification);
}
