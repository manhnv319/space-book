package com.velstrong.bookstore.domain.port.in.voucher;

import com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand;

public interface CommitVoucherUseCase {
    void commit(CommitVoucherCommand command);
}
