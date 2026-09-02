package com.velstrong.bookstore.infrastructure.adapter.in.rest.voucher;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherDiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record VoucherRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        @NotNull VoucherDiscountType discountType,
        @NotNull Long discountValue,
        Long maxDiscountAmount,
        Long minOrderAmount,
        @NotNull LocalDateTime startAt,
        @NotNull LocalDateTime endAt,
        Integer usageLimitTotal,
        Integer usageLimitPerUser
) {
    public CreateVoucherCommand toCommand() {
        return new CreateVoucherCommand(code, name, description, discountType, discountValue,
                maxDiscountAmount, minOrderAmount, startAt, endAt, usageLimitTotal, usageLimitPerUser);
    }
}
