package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.voucher.UpdateVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateVoucherService implements UpdateVoucherUseCase {

    private final VoucherRepository voucherRepository;

    public UpdateVoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public VoucherResponse update(Long voucherId, CreateVoucherCommand command) {
        var voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new EntityNotFoundException("Voucher", voucherId));
        voucher.update(command.name(), command.description(), command.discountValue(),
                command.maxDiscountAmount(), command.minOrderAmount(), command.startAt(), command.endAt(),
                command.usageLimitTotal(), command.usageLimitPerUser());
        return VoucherResponse.from(voucherRepository.save(voucher));
    }
}
