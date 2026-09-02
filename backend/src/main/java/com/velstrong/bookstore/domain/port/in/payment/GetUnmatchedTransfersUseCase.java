package com.velstrong.bookstore.domain.port.in.payment;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.UnmatchedTransferResponse;

public interface GetUnmatchedTransfersUseCase {
    PagedResponse<UnmatchedTransferResponse> getAll(int page, int size);
}
