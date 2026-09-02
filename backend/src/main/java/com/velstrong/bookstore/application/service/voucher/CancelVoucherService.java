package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.domain.model.VoucherUsage;
import com.velstrong.bookstore.domain.port.in.voucher.CancelVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import com.velstrong.bookstore.domain.port.out.VoucherUsageRepository;


@Service
@Transactional
public class CancelVoucherService implements CancelVoucherUseCase {

    private final VoucherUsageRepository voucherUsageRepository;
    private final VoucherRepository voucherRepository;

    public CancelVoucherService(VoucherUsageRepository voucherUsageRepository,
                                VoucherRepository voucherRepository) {
        this.voucherUsageRepository = voucherUsageRepository;
        this.voucherRepository = voucherRepository;
    }

    @Override
    public void cancelReservation(Long orderId) {
        voucherUsageRepository.findReservedByOrderId(orderId).ifPresent(usage -> {
            usage.cancel();
            voucherUsageRepository.save(usage);
            voucherRepository.decrementUsage(usage.getVoucherId());
        });
    }
}
