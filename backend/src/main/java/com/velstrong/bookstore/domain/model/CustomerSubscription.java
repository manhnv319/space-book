package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.subscription.CustomerSubscriptionStatus;

import java.time.LocalDate;

public class CustomerSubscription {

    private final Long id;
    private final Long userId;
    private final Long subscriptionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer usedRentals;
    private CustomerSubscriptionStatus status;
    private Subscription subscription;

    private CustomerSubscription(Long id, Long userId, Long subscriptionId, LocalDate startDate,
                                  LocalDate endDate, Integer usedRentals,
                                  CustomerSubscriptionStatus status, Subscription subscription) {
        this.id = id;
        this.userId = userId;
        this.subscriptionId = subscriptionId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usedRentals = usedRentals;
        this.status = status;
        this.subscription = subscription;
    }

    /**
     * Gói mới đặt mua, CHƯA dùng được.
     *
     * Trước đây gói kích hoạt ngay lúc bấm mua, tức khách dùng được mà chưa trả
     * đồng nào. Thời hạn tính từ lúc tiền về ({@link #activate}), không tính từ
     * lúc đặt, để khách không mất ngày nào vì thời gian chờ chuyển khoản.
     */
    public static CustomerSubscription createPendingPayment(Long userId, Long subscriptionId) {
        return new CustomerSubscription(null, userId, subscriptionId, null, null, 0,
                CustomerSubscriptionStatus.PENDING_PAYMENT, null);
    }

    /** Tiền đã về: bắt đầu tính thời hạn từ hôm nay. */
    public void activate(LocalDate startDate, int durationDays) {
        if (status != CustomerSubscriptionStatus.PENDING_PAYMENT)
            throw new IllegalStateException("Subscription is not awaiting payment");
        this.startDate = startDate;
        this.endDate = startDate.plusDays(durationDays);
        this.status = CustomerSubscriptionStatus.ACTIVE;
    }

    public static CustomerSubscription reconstitute(Long id, Long userId, Long subscriptionId,
                                                     LocalDate startDate, LocalDate endDate,
                                                     Integer usedRentals, CustomerSubscriptionStatus status,
                                                     Subscription subscription) {
        return new CustomerSubscription(id, userId, subscriptionId, startDate, endDate,
                usedRentals, status, subscription);
    }

    public void cancel() {
        if (!status.isActive()) throw new InvalidOperationException("Subscription is not active");
        this.status = CustomerSubscriptionStatus.CANCELLED;
    }

    public void extend(int additionalDays) {
        if (!status.isActive()) throw new InvalidOperationException("Cannot extend inactive subscription");
        this.endDate = endDate.plusDays(additionalDays);
    }

    public void incrementUsedRentals() {
        this.usedRentals = (usedRentals != null ? usedRentals : 0) + 1;
    }

    public boolean isActive(LocalDate today) { return status != null && status.isActive() && !today.isAfter(endDate); }
    public boolean hasRentalQuota(int maxRentals) { return usedRentals == null || usedRentals < maxRentals; }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getSubscriptionId() { return subscriptionId; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Integer getUsedRentals() { return usedRentals; }
    public CustomerSubscriptionStatus getStatus() { return status; }
    public Subscription getSubscription() { return subscription; }
    public void setSubscription(Subscription subscription) { this.subscription = subscription; }
}
