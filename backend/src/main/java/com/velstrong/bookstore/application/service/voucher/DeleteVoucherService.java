package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.domain.port.in.voucher.DeleteVoucherUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteVoucherService implements DeleteVoucherUseCase {

    private final VoucherRepository voucherRepository;

    public DeleteVoucherService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public void delete(Long voucherId) {
        voucherRepository.deleteById(voucherId);
    }
}
