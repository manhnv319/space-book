package com.velstrong.bookstore.application.service.voucher;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.voucher.VoucherResponse;
import com.velstrong.bookstore.domain.port.in.voucher.GetAllVouchersUseCase;
import com.velstrong.bookstore.domain.port.out.VoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional(readOnly = true)
public class GetAllVouchersService implements GetAllVouchersUseCase {

    private final VoucherRepository voucherRepository;

    public GetAllVouchersService(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    public PagedResponse<VoucherResponse> getAll(Byte status, String discountType, String search,
                                                 LocalDate fromDate, LocalDate toDate, int page, int size) {
        var result = voucherRepository.findAll(status, discountType, search, fromDate, toDate, page, size);
        return PagedResponse.of(
                result.content().stream().map(VoucherResponse::from).toList(),
                page, size, result.totalElements());
    }
}
