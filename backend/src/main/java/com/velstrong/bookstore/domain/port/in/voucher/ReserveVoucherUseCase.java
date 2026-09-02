package com.velstrong.bookstore.domain.port.in.voucher;

import com.velstrong.bookstore.application.command.voucher.ReserveVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;

public interface ReserveVoucherUseCase {
    VoucherQuoteResponse reserve(ReserveVoucherCommand command);
}
