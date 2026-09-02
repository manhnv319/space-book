package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BankTransferReconciliationRepository {
    boolean existsProcessed(String messageId, String bankTxnRef);
    void saveProcessed(String messageId, String bankTxnRef, LocalDateTime processedAt);
    void saveUnmatched(String messageId, String bankTxnRef, String paymentReference, long amount,
                       LocalDateTime receivedAt, String reason, LocalDateTime createdAt);

    /** Newest first — an operator chasing a missing payment looks at what just arrived. */
    PageResult<UnmatchedTransfer> findUnmatched(int page, int size);
    Optional<UnmatchedTransfer> findById(Long id);
    void deleteById(Long id);
}
