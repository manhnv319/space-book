package com.velstrong.bookstore.domain.port.in.voucher;

import com.velstrong.bookstore.application.response.voucher.VoucherResponse;

public interface GetVoucherUseCase {
    VoucherResponse getById(Long voucherId);
}
