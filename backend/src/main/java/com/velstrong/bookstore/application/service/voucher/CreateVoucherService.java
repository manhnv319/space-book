package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.voucher.CreateVoucherCommand;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;
import com.velstrong.bookstore.domain.exception.DuplicateEntityException;
import com.velstrong.bookstore.domain.model.Voucher;
import com.velstrong.bookstore.domain.port.in.voucher.CreateVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;


@Service
@Transactional
public class CreateVoucherService implements CreateVoucherUseCase {

    private final VoucherRepository voucherRepository;

    public CreateVoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public VoucherResponse create(CreateVoucherCommand command) {
        if (voucherRepository.findByCode(command.code()).isPresent())
            throw new DuplicateEntityException("Voucher", "code", command.code());

        Voucher voucher = Voucher.create(
                command.code(), command.name(), command.discountType(), command.discountValue(),
                command.maxDiscountAmount(), command.minOrderAmount(),
                command.startAt(), command.endAt(),
                command.usageLimitTotal(), command.usageLimitPerUser()
        );
        return VoucherResponse.from(voucherRepository.save(voucher));
    }
}
