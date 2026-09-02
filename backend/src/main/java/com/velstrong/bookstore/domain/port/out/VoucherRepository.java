package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Voucher;

import java.time.LocalDate;
import java.util.Optional;

public interface VoucherRepository {
    Voucher save(Voucher voucher);
    Optional<Voucher> findById(Long id);
    Optional<Voucher> findByCode(String code);
    PageResult<Voucher> findAll(Byte status, String discountType, String search,
                                 LocalDate fromDate, LocalDate toDate, int page, int size);
    void deleteById(Long id);
    boolean tryIncrementUsage(Long voucherId);
    boolean decrementUsage(Long voucherId);
}
