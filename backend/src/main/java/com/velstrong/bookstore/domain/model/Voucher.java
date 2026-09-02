package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;

import java.time.LocalDateTime;

public class Voucher {

    private final Long id;
    private final String code;
    private String name;
    private String description;
    private final VoucherDiscountType discountType;
    private Long discountValue;
    private Long maxDiscountAmount;
    private Long minOrderAmount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private Integer usageLimitTotal;
    private Integer usageLimitPerUser;
    private Integer usedCount;
    private Byte status;

    private Voucher(Long id, String code, String name, String description,
                    VoucherDiscountType discountType, Long discountValue, Long maxDiscountAmount,
                    Long minOrderAmount, LocalDateTime startAt, LocalDateTime endAt,
                    Integer usageLimitTotal, Integer usageLimitPerUser, Integer usedCount, Byte status) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usageLimitTotal = usageLimitTotal;
        this.usageLimitPerUser = usageLimitPerUser;
        this.usedCount = usedCount;
        this.status = status;
    }

    public static Voucher create(String code, String name, VoucherDiscountType discountType,
                                 Long discountValue, Long maxDiscountAmount, Long minOrderAmount,
                                 LocalDateTime startAt, LocalDateTime endAt,
                                 Integer usageLimitTotal, Integer usageLimitPerUser) {
        return new Voucher(null, code, name, null, discountType, discountValue,
                maxDiscountAmount, minOrderAmount, startAt, endAt,
                usageLimitTotal, usageLimitPerUser, 0, (byte) 1);
    }

    public static Voucher reconstitute(Long id, String code, String name, String description,
                                       VoucherDiscountType discountType, Long discountValue,
                                       Long maxDiscountAmount, Long minOrderAmount,
                                       LocalDateTime startAt, LocalDateTime endAt,
                                       Integer usageLimitTotal, Integer usageLimitPerUser,
                                       Integer usedCount, Byte status) {
        return new Voucher(id, code, name, description, discountType, discountValue,
                maxDiscountAmount, minOrderAmount, startAt, endAt, usageLimitTotal, usageLimitPerUser,
                usedCount, status);
    }

    public VoucherValidationReason validate(Long baseAmount, LocalDateTime now) {
        if (status == null || status == 0) return VoucherValidationReason.INACTIVE;
        if (startAt != null && now.isBefore(startAt)) return VoucherValidationReason.NOT_YET_ACTIVE;
        if (endAt != null && now.isAfter(endAt)) return VoucherValidationReason.EXPIRED;
        if (minOrderAmount != null && baseAmount < minOrderAmount) return VoucherValidationReason.MIN_ORDER_NOT_MET;
        return null;
    }

    public Long calculateDiscount(Long baseAmount) {
        if (discountType == VoucherDiscountType.PERCENTAGE) {
            long discount = baseAmount * discountValue / 100;
            return maxDiscountAmount != null ? Math.min(discount, maxDiscountAmount) : discount;
        }
        return Math.min(discountValue, baseAmount);
    }

    public void update(String name, String description, Long discountValue,
                       Long maxDiscountAmount, Long minOrderAmount,
                       LocalDateTime startAt, LocalDateTime endAt,
                       Integer usageLimitTotal, Integer usageLimitPerUser) {
        if (discountValue == null || discountValue <= 0) {
            throw new InvalidOperationException("Discount value must be positive");
        }
        this.name = name;
        this.description = description;
        this.discountValue = discountValue;
        this.maxDiscountAmount = maxDiscountAmount;
        this.minOrderAmount = minOrderAmount;
        this.startAt = startAt;
        this.endAt = endAt;
        this.usageLimitTotal = usageLimitTotal;
        this.usageLimitPerUser = usageLimitPerUser;
    }

    public boolean isActive() { return status != null && status == 1; }
    public void deactivate() { this.status = 0; }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public VoucherDiscountType getDiscountType() { return discountType; }
    public Long getDiscountValue() { return discountValue; }
    public Long getMaxDiscountAmount() { return maxDiscountAmount; }
    public Long getMinOrderAmount() { return minOrderAmount; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public Integer getUsageLimitTotal() { return usageLimitTotal; }
    public Integer getUsageLimitPerUser() { return usageLimitPerUser; }
    public Integer getUsedCount() { return usedCount; }
    public Byte getStatus() { return status; }
}
