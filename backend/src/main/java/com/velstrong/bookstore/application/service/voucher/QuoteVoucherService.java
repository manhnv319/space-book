package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.enums.voucher.VoucherValidationReason;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class QuoteVoucherService implements QuoteVoucherUseCase {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;

    public QuoteVoucherService(VoucherRepository voucherRepository,
                                VoucherUsageRepository voucherUsageRepository) {
        this.voucherRepository = voucherRepository;
        this.voucherUsageRepository = voucherUsageRepository;
    }

    @Override
    public VoucherQuoteResponse quote(QuoteVoucherCommand command) {
        Voucher voucher = voucherRepository.findByCode(command.voucherCode()).orElse(null);
        if (voucher == null) return VoucherQuoteResponse.invalid(VoucherValidationReason.NOT_FOUND);

        VoucherValidationReason reason = voucher.validate(command.baseAmount(), LocalDateTime.now());
        if (reason != null) return VoucherQuoteResponse.invalid(reason);

        if (voucher.getUsageLimitTotal() != null) {
            int totalUsed = voucherUsageRepository.countCommittedByVoucherId(voucher.getId());
            if (totalUsed >= voucher.getUsageLimitTotal())
                return VoucherQuoteResponse.invalid(VoucherValidationReason.USAGE_LIMIT_REACHED);
        }

        if (voucher.getUsageLimitPerUser() != null && command.userId() != null) {
            int userUsed = voucherUsageRepository.countCommittedByVoucherIdAndUserId(voucher.getId(), command.userId());
            if (userUsed >= voucher.getUsageLimitPerUser())
                return VoucherQuoteResponse.invalid(VoucherValidationReason.USER_LIMIT_REACHED);
        }

        Long discount = voucher.calculateDiscount(command.baseAmount());
        Long finalAmount = command.baseAmount() - discount;
        return VoucherQuoteResponse.valid(discount, finalAmount);
    }
}
