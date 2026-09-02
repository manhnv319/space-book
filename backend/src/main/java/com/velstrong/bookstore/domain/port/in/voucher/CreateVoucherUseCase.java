package com.velstrong.bookstore.domain.port.in.voucher;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;

public interface CreateVoucherUseCase {
    VoucherResponse create(CreateVoucherCommand command);
}
