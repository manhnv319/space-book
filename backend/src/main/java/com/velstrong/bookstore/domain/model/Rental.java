package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Rental {

    private final Long id;
    private final Long orderItemId;
    private final Long bookCopyId;
    private final Long userId;
    private final RentalTermUnit rentalTermUnit;
    private final Integer rentalTermValue;
    private final Long depositAmount;
    private final LocalDate rentalStartDate;
    private final LocalDate plannedReturnDate;
    private LocalDate actualReturnDate;
    private RentalStatus status;
    private Integer lateDays;
    private Long lateFeeAmount;
    private Long damageFeeAmount;
    private String notes;
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    private Rental(Long id, Long orderItemId, Long bookCopyId, Long userId,
                   RentalTermUnit rentalTermUnit, Integer rentalTermValue, Long depositAmount,
                   LocalDate rentalStartDate, LocalDate plannedReturnDate, LocalDate actualReturnDate,
                   RentalStatus status, Integer lateDays, Long lateFeeAmount, Long damageFeeAmount,
                   String notes, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.orderItemId = orderItemId;
        this.bookCopyId = bookCopyId;
        this.userId = userId;
        this.rentalTermUnit = rentalTermUnit;
        this.rentalTermValue = rentalTermValue;
        this.depositAmount = depositAmount;
        this.rentalStartDate = rentalStartDate;
        this.plannedReturnDate = plannedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
        this.lateDays = lateDays;
        this.lateFeeAmount = lateFeeAmount;
        this.damageFeeAmount = damageFeeAmount;
        this.notes = notes;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Rental create(Long orderItemId, Long bookCopyId, Long userId,
                                RentalTermUnit termUnit, int termValue, Long depositAmount,
                                LocalDate startDate, LocalDate plannedReturnDate) {
        return new Rental(null, orderItemId, bookCopyId, userId, termUnit, termValue,
                depositAmount, startDate, plannedReturnDate, null,
                RentalStatus.RENTED, 0, 0L, 0L, null, LocalDateTime.now(), null);
    }

    public static Rental reconstitute(Long id, Long orderItemId, Long bookCopyId, Long userId,
                                      RentalTermUnit rentalTermUnit, Integer rentalTermValue,
                                      Long depositAmount, LocalDate rentalStartDate,
                                      LocalDate plannedReturnDate, LocalDate actualReturnDate,
                                      RentalStatus status, Integer lateDays, Long lateFeeAmount,
                                      Long damageFeeAmount, String notes,
                                      LocalDateTime createdAt, LocalDateTime modifiedAt) {
        return new Rental(id, orderItemId, bookCopyId, userId, rentalTermUnit, rentalTermValue,
                depositAmount, rentalStartDate, plannedReturnDate, actualReturnDate,
                status, lateDays, lateFeeAmount, damageFeeAmount, notes, createdAt, modifiedAt);
    }

    public void returnBook(LocalDate today) {
        if (!canReturn()) throw new InvalidOperationException("Cannot return rental with status: " + status);
        this.actualReturnDate = today;
        this.status = RentalStatus.RETURNED;
        this.modifiedAt = LocalDateTime.now();
        calculateLateDays(today);
    }

    public void calculateLateDays(LocalDate today) {
        LocalDate returnDate = actualReturnDate != null ? actualReturnDate : today;
        if (plannedReturnDate != null && returnDate.isAfter(plannedReturnDate)) {
            this.lateDays = (int) ChronoUnit.DAYS.between(plannedReturnDate, returnDate);
        } else {
            this.lateDays = 0;
        }
    }

    public void calculateLateFee(long feePerDay, LocalDate today) {
        calculateLateDays(today);
        this.lateFeeAmount = lateDays != null && lateDays > 0 ? lateDays * feePerDay : 0L;
    }

    public void applyDamageFee(Long damageFeeAmount) {
        this.damageFeeAmount = damageFeeAmount != null ? damageFeeAmount : 0L;
    }

    public Long calculateRefundAmount() {
        long deposit = depositAmount != null ? depositAmount : 0L;
        long late = lateFeeAmount != null ? lateFeeAmount : 0L;
        long damage = damageFeeAmount != null ? damageFeeAmount : 0L;
        long refund = deposit - late - damage;
        return refund > 0 ? refund : 0L;
    }

    public boolean isOverdue(LocalDate today) {
        if (actualReturnDate != null || plannedReturnDate == null) return false;
        return today.isAfter(plannedReturnDate);
    }

    public boolean canReturn() { return status != null && status.isActive(); }
    public boolean isActive() { return status != null && status.isActive(); }

    public Long getId() { return id; }
    public Long getOrderItemId() { return orderItemId; }
    public Long getBookCopyId() { return bookCopyId; }
    public Long getUserId() { return userId; }
    public RentalTermUnit getRentalTermUnit() { return rentalTermUnit; }
    public Integer getRentalTermValue() { return rentalTermValue; }
    public Long getDepositAmount() { return depositAmount; }
    public LocalDate getRentalStartDate() { return rentalStartDate; }
    public LocalDate getPlannedReturnDate() { return plannedReturnDate; }
    public LocalDate getActualReturnDate() { return actualReturnDate; }
    public RentalStatus getStatus() { return status; }
    public Integer getLateDays() { return lateDays; }
    public Long getLateFeeAmount() { return lateFeeAmount; }
    public Long getDamageFeeAmount() { return damageFeeAmount; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getModifiedAt() { return modifiedAt; }
    public void setStatus(RentalStatus status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setLateFeeAmount(Long lateFeeAmount) { this.lateFeeAmount = lateFeeAmount; }
    public void setDamageFeeAmount(Long damageFeeAmount) { this.damageFeeAmount = damageFeeAmount; }
}
