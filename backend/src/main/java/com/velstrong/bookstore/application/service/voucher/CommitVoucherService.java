package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.voucher.CommitVoucherCommand;
import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.port.in.voucher.CommitVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;


@Service
@Transactional
public class CommitVoucherService implements CommitVoucherUseCase {

    private final VoucherUsageRepository voucherUsageRepository;

    public CommitVoucherService(VoucherUsageRepository voucherUsageRepository) {
        this.voucherUsageRepository = voucherUsageRepository;
    }

    @Override
    public void commit(CommitVoucherCommand command) {
        voucherUsageRepository.findReservedByOrderId(command.orderId()).ifPresent(usage -> {
            usage.commit();
            voucherUsageRepository.save(usage);
        });
    }
}
