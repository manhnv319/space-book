package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.rental.RentalFulfillmentStatus;

import java.time.LocalDateTime;

public class RentalFulfillment {

    private final Long id;
    private final Long orderId;
    private RentalFulfillmentStatus status;
    private int attempts;
    private String errorMessage;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    private RentalFulfillment(Long id, Long orderId, RentalFulfillmentStatus status, int attempts,
                              String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt,
                              LocalDateTime completedAt) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.attempts = attempts;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
    }

    public static RentalFulfillment pending(Long orderId) {
        LocalDateTime now = LocalDateTime.now();
        return new RentalFulfillment(null, orderId, RentalFulfillmentStatus.PENDING, 0, null, now, now, null);
    }

    public static RentalFulfillment reconstitute(Long id, Long orderId, RentalFulfillmentStatus status, int attempts,
                                                  String errorMessage, LocalDateTime createdAt, LocalDateTime updatedAt,
                                                  LocalDateTime completedAt) {
        return new RentalFulfillment(id, orderId, status, attempts, errorMessage, createdAt, updatedAt, completedAt);
    }

    public boolean canRetry() { return status != RentalFulfillmentStatus.COMPLETED; }

    public void beginAttempt() {
        attempts++;
        status = RentalFulfillmentStatus.PENDING;
        errorMessage = null;
        updatedAt = LocalDateTime.now();
    }

    public void fail(String message) {
        status = RentalFulfillmentStatus.FAILED;
        errorMessage = message != null ? message.substring(0, Math.min(message.length(), 1000)) : "Unknown error";
        updatedAt = LocalDateTime.now();
    }

    public void complete() {
        status = RentalFulfillmentStatus.COMPLETED;
        errorMessage = null;
        completedAt = LocalDateTime.now();
        updatedAt = completedAt;
    }

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public RentalFulfillmentStatus getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
