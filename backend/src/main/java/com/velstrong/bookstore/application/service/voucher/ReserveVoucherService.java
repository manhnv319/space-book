package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.command.voucher.ReserveVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.ReserveVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;


@Service
@Transactional
public class ReserveVoucherService implements ReserveVoucherUseCase {

    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final QuoteVoucherUseCase quoteVoucherUseCase;

    public ReserveVoucherService(VoucherRepository voucherRepository,
                                  VoucherUsageRepository voucherUsageRepository,
                                  QuoteVoucherUseCase quoteVoucherUseCase) {
        this.voucherRepository = voucherRepository;
        this.voucherUsageRepository = voucherUsageRepository;
        this.quoteVoucherUseCase = quoteVoucherUseCase;
    }

    @Override
    public VoucherQuoteResponse reserve(ReserveVoucherCommand command) {
        VoucherQuoteResponse quote = quoteVoucherUseCase.quote(
                new QuoteVoucherCommand(command.userId(), command.voucherCode(), command.baseAmount()));
        if (!quote.valid()) throw new InvalidOperationException("Voucher is not valid: " + quote.reason());

        Voucher voucher = voucherRepository.findByCode(command.voucherCode())
                .orElseThrow(() -> new EntityNotFoundException("Voucher", command.voucherCode()));

        if (!voucherRepository.tryIncrementUsage(voucher.getId()))
            throw new InvalidOperationException("Voucher has reached its usage limit");

        VoucherUsage usage = VoucherUsage.reserve(voucher.getId(), command.userId(),
                command.orderId(), quote.discountAmount());
        voucherUsageRepository.save(usage);

        return quote;
    }
}
