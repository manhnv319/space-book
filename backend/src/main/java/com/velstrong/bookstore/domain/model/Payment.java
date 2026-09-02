package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentTransactionStatus;

import java.time.LocalDateTime;

public class Payment {

    private final Long id;
    private final Long orderId;
    private final Long customerSubscriptionId;
    private final Long amount;
    private final PaymentMethod method;
    private PaymentTransactionStatus status;
    private String transactionId;
    private String gatewayRef;
    private String transferReference;
    private LocalDateTime expiresAt;
    private LocalDateTime paidAt;
    private final LocalDateTime createdAt;

    private Payment(Long id, Long orderId, Long customerSubscriptionId, Long amount, PaymentMethod method,
                    PaymentTransactionStatus status, String transactionId, String gatewayRef,
                    String transferReference, LocalDateTime expiresAt, LocalDateTime paidAt, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerSubscriptionId = customerSubscriptionId;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.transactionId = transactionId;
        this.gatewayRef = gatewayRef;
        this.transferReference = transferReference;
        this.expiresAt = expiresAt;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
    }

    public static Payment create(Long orderId, Long amount, PaymentMethod method) {
        return new Payment(null, orderId, null, amount, method,
                PaymentTransactionStatus.PENDING, null, null, null, null, null, LocalDateTime.now());
    }

    /** Khoản tiền của một gói thuê tháng — không gắn với đơn hàng nào. */
    public static Payment createForSubscription(Long customerSubscriptionId, Long amount, PaymentMethod method) {
        return new Payment(null, null, customerSubscriptionId, amount, method,
                PaymentTransactionStatus.PENDING, null, null, null, null, null, LocalDateTime.now());
    }

    public boolean isForSubscription() {
        return customerSubscriptionId != null;
    }

    public Long getCustomerSubscriptionId() {
        return customerSubscriptionId;
    }

    public static Payment reconstitute(Long id, Long orderId, Long amount, PaymentMethod method,
                                       PaymentTransactionStatus status, String transactionId, String gatewayRef,
                                       String transferReference, LocalDateTime expiresAt,
                                       LocalDateTime paidAt, LocalDateTime createdAt) {
        return reconstitute(id, orderId, null, amount, method, status, transactionId, gatewayRef,
                transferReference, expiresAt, paidAt, createdAt);
    }

    public static Payment reconstitute(Long id, Long orderId, Long customerSubscriptionId, Long amount,
                                       PaymentMethod method, PaymentTransactionStatus status, String transactionId,
                                       String gatewayRef, String transferReference, LocalDateTime expiresAt,
                                       LocalDateTime paidAt, LocalDateTime createdAt) {
        return new Payment(id, orderId, customerSubscriptionId, amount, method, status, transactionId, gatewayRef,
                transferReference, expiresAt, paidAt, createdAt);
    }

    public static Payment reconstitute(Long id, Long orderId, Long amount, PaymentMethod method,
                                       PaymentTransactionStatus status, String transactionId, String gatewayRef,
                                       LocalDateTime paidAt, LocalDateTime createdAt) {
        return reconstitute(id, orderId, amount, method, status, transactionId, gatewayRef,
                null, null, paidAt, createdAt);
    }

    public void initializeBankTransfer(String reference, LocalDateTime expiry) {
        if (method != PaymentMethod.BANK_TRANSFER || !status.isPending())
            throw new IllegalStateException("Bank transfer payment is not pending");
        this.transferReference = reference;
        this.expiresAt = expiry;
    }

    public void markSuccess(String transactionId, String gatewayRef) {
        this.status = PaymentTransactionStatus.SUCCESS;
        this.transactionId = transactionId;
        this.gatewayRef = gatewayRef;
        this.paidAt = LocalDateTime.now();
    }

    public boolean isExpired(LocalDateTime now) { return expiresAt != null && !expiresAt.isAfter(now); }
    public void markFailed() { this.status = PaymentTransactionStatus.FAILED; }
    public void markRefunded() { this.status = PaymentTransactionStatus.REFUNDED; }

    public boolean isSuccess() { return status != null && status.isSuccess(); }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentTransactionStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getGatewayRef() { return gatewayRef; }
    public String getTransferReference() { return transferReference; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
