package com.velstrong.bookstore.application.command.voucher;

import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;

import java.time.LocalDateTime;

public record CreateVoucherCommand(
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
        Integer usageLimitPerUser
) {}
