package com.velstrong.bookstore.application.service.voucher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.voucher.VoucherResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.voucher.GetVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;

@Service
@Transactional(readOnly = true)
public class GetVoucherService implements GetVoucherUseCase {

    private final VoucherRepository voucherRepository;

    public GetVoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public VoucherResponse getById(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .map(VoucherResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("Voucher", voucherId));
    }
}
