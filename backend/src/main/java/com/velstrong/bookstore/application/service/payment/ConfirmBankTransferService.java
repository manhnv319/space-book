package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.command.payment.BankTransferNotification;
import com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRecordService;
import com.velstrong.bookstore.application.service.rental.RentalFulfillmentRetryService;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.port.in.payment.ConfirmBankTransferUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.BankTransferReconciliationRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.CustomerSubscriptionRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import com.velstrong.bookstore.domain.port.out.SubscriptionRepository;
import com.velstrong.bookstore.domain.model.CustomerSubscription;
import com.velstrong.bookstore.domain.model.Subscription;

import java.time.LocalDate;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

@Service
@Transactional
public class ConfirmBankTransferService implements ConfirmBankTransferUseCase {
    private final PaymentRepository payments; private final OrderRepository orders; private final CommitVoucherUseCase vouchers;
    private final RentalFulfillmentRecordService records; private final RentalFulfillmentRetryService retries;
    private final BankTransferReconciliationRepository reconciliation;
    private final OrderStatusHistoryRepository statusHistory;
    private final CustomerSubscriptionRepository customerSubscriptions;
    private final SubscriptionRepository subscriptions;

    public ConfirmBankTransferService(PaymentRepository payments, OrderRepository orders, CommitVoucherUseCase vouchers,
                                      RentalFulfillmentRecordService records, RentalFulfillmentRetryService retries,
                                      BankTransferReconciliationRepository reconciliation,
                                      OrderStatusHistoryRepository statusHistory,
                                      CustomerSubscriptionRepository customerSubscriptions,
                                      SubscriptionRepository subscriptions) {
        this.payments = payments; this.orders = orders; this.vouchers = vouchers; this.records = records; this.retries = retries;
        this.reconciliation = reconciliation;
        this.statusHistory = statusHistory;
        this.customerSubscriptions = customerSubscriptions;
        this.subscriptions = subscriptions;
    }

    @Override
    public void confirm(BankTransferNotification event) {
        if (reconciliation.existsProcessed(event.messageId(), event.transactionReference())) return;
        saveProcessed(event);
        Payment payment = payments.findByTransferReference(event.paymentReference()).orElse(null);
        if (payment == null || payment.getMethod() != PaymentMethod.BANK_TRANSFER || payment.isSuccess()
                || payment.isExpired(LocalDateTime.now()) || payment.getAmount() != event.amount()) {
            saveUnmatched(event, "No matching pending payment with exact amount"); return;
        }
        if (payment.isForSubscription()) {
            activateSubscription(payment, event);
            return;
        }

        Order order = orders.findByIdForUpdate(payment.getOrderId()).orElse(null);
        if (order == null) { saveUnmatched(event, "Order not found"); return; }
        payment.markSuccess(event.transactionReference(), "TIMO_IMAP");
        payments.save(payment);
        order.markPaid();
        // Thanh toán xong là đơn được xác nhận — nếu không đẩy trạng thái ở đây,
        // đơn nằm mãi ở PENDING và lộ trình giao hàng không bao giờ khởi động.
        order.updateStatus(OrderStatus.CONFIRMED);
        orders.save(order);
        statusHistory.record(order.getId(), OrderStatus.CONFIRMED, OrderStatusChange.SOURCE_PAYMENT, LocalDateTime.now());
        vouchers.commit(new CommitVoucherCommand(order.getId())); scheduleRentalFulfillment(order);
    }

    /**
     * Tiền về cho một gói thuê tháng.
     *
     * Thời hạn tính từ hôm nay chứ không từ lúc đặt mua, để khách không mất ngày
     * nào vì thời gian chờ chuyển khoản.
     */
    private void activateSubscription(Payment payment, BankTransferNotification event) {
        CustomerSubscription pending = customerSubscriptions.findById(payment.getCustomerSubscriptionId())
                .orElse(null);
        if (pending == null || !pending.getStatus().isAwaitingPayment()) {
            saveUnmatched(event, "Subscription is not awaiting payment");
            return;
        }
        Subscription plan = subscriptions.findById(pending.getSubscriptionId()).orElse(null);
        if (plan == null) {
            saveUnmatched(event, "Subscription plan not found");
            return;
        }

        payment.markSuccess(event.transactionReference(), "TIMO_IMAP");
        payments.save(payment);
        pending.activate(LocalDate.now(), plan.getDurationDays());
        customerSubscriptions.save(pending);
    }

    private void saveProcessed(BankTransferNotification event) {
        reconciliation.saveProcessed(event.messageId(), event.transactionReference(), LocalDateTime.now());
    }
    private void saveUnmatched(BankTransferNotification e, String reason) {
        reconciliation.saveUnmatched(e.messageId(), e.transactionReference(), e.paymentReference(),
                e.amount(), e.occurredAt(), reason, LocalDateTime.now());
    }
    private void scheduleRentalFulfillment(Order order) {
        if (!order.isRentalOrder() && !order.isMixedOrder()) return;
        records.ensurePending(order.getId());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { retries.process(order.getId()); }
        });
    }
}
