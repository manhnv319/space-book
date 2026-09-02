package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.ResolveUnmatchedTransferCommand;
import com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRecordService;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRetryService;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.UnmatchedTransfer;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.in.payment.ResolveUnmatchedTransferUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.BankTransferReconciliationRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service @Transactional
public class ResolveUnmatchedTransferService implements ResolveUnmatchedTransferUseCase {
    private final BankTransferReconciliationRepository transfers; private final PaymentRepository payments; private final OrderRepository orders;
    private final OrderStatusHistoryRepository history; private final CommitVoucherUseCase vouchers; private final RentalFulfillmentRecordService records; private final RentalFulfillmentRetryService retries;
    public ResolveUnmatchedTransferService(BankTransferReconciliationRepository transfers, PaymentRepository payments, OrderRepository orders, OrderStatusHistoryRepository history, CommitVoucherUseCase vouchers, RentalFulfillmentRecordService records, RentalFulfillmentRetryService retries) { this.transfers = transfers; this.payments = payments; this.orders = orders; this.history = history; this.vouchers = vouchers; this.records = records; this.retries = retries; }
    @Override public void resolve(ResolveUnmatchedTransferCommand command) {
        UnmatchedTransfer transfer = transfers.findById(command.transferId()).orElseThrow(() -> new EntityNotFoundException("UnmatchedTransfer", command.transferId()));
        Order order = orders.findByIdForUpdate(command.orderId()).orElseThrow(() -> new EntityNotFoundException("Order", command.orderId()));
        Payment payment = payments.findByOrderId(order.getId()).orElseThrow(() -> new InvalidOperationException("Order has no payment"));
        if (payment.getMethod() != PaymentMethod.BANK_TRANSFER || payment.isSuccess() || transfer.amount() == null || !transfer.amount().equals(payment.getAmount())) throw new InvalidOperationException("Transfer cannot be matched to this order");
        payment.markSuccess("MANUAL-" + transfer.id(), "MANUAL_RECONCILIATION"); payments.save(payment); order.markPaid(); order.updateStatus(OrderStatus.CONFIRMED); orders.save(order);
        history.record(order.getId(), OrderStatus.CONFIRMED, OrderStatusChange.SOURCE_PAYMENT, LocalDateTime.now()); vouchers.commit(new CommitVoucherCommand(order.getId())); transfers.deleteById(transfer.id());
        if (order.isRentalOrder() || order.isMixedOrder()) { records.ensurePending(order.getId()); TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { @Override public void afterCommit() { retries.process(order.getId()); } }); }
    }
}
