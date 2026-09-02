package com.velstrong.bookstore.domain.port.in.voucher;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;

import java.time.LocalDate;

public interface GetAllVouchersUseCase {
    PagedResponse<VoucherResponse> getAll(Byte status, String discountType, String search,
                                          LocalDate fromDate, LocalDate toDate, int page, int size);
}
