package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.payment.UnmatchedTransferResponse;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;
import com.velstrong.bookstore.domain.port.in.payment.GetUnmatchedTransfersUseCase;
import com.velstrong.bookstore.domain.port.out.BankTransferReconciliationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lists credits the reconciler could not attach to an order.
 *
 * Without this the money is simply gone from the operator's point of view: the
 * poller records the row and nothing ever surfaces it. A customer who mistyped
 * the transfer memo has paid and has no order.
 */
@Service
@Transactional(readOnly = true)
public class GetUnmatchedTransfersService implements GetUnmatchedTransfersUseCase {

    private final BankTransferReconciliationRepository reconciliation;

    public GetUnmatchedTransfersService(BankTransferReconciliationRepository reconciliation) {
        this.reconciliation = reconciliation;
    }

    @Override
    public PagedResponse<UnmatchedTransferResponse> getAll(int page, int size) {
        PageResult<UnmatchedTransfer> result = reconciliation.findUnmatched(page, size);
        return PagedResponse.of(result.content().stream().map(UnmatchedTransferResponse::from).toList(),
                page, size, result.totalElements());
    }
}
