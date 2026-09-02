package com.velstrong.bookstore.application.response.voucher;

import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;

import java.time.LocalDateTime;

public record VoucherResponse(
        Long id,
        String code,
        String name,
        String description,
        VoucherDiscountType discountType,
        Long discountValue,
        Long maxDiscountAmount,
        Long minOrderAmount,
        LocalDateTime startAt,
        LocalDateTime endAt,
        Integer usageLimitTotal,
        Integer usageLimitPerUser,
        Byte status
) {
    public static VoucherResponse from(Voucher v) {
        return new VoucherResponse(v.getId(), v.getCode(), v.getName(), v.getDescription(),
                v.getDiscountType(), v.getDiscountValue(), v.getMaxDiscountAmount(),
                v.getMinOrderAmount(), v.getStartAt(), v.getEndAt(),
                v.getUsageLimitTotal(), v.getUsageLimitPerUser(), v.getStatus());
    }
}
