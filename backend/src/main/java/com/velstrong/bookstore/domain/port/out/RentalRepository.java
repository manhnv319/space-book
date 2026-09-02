package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;

import java.util.List;
import java.util.Optional;

public interface RentalRepository {
    Rental save(Rental rental);
    List<Rental> saveAll(List<Rental> rentals);
    Optional<Rental> findById(Long id);
    boolean existsByOrderItemId(Long orderItemId);
    PageResult<Rental> findByUserId(Long userId, RentalStatus status, int page, int size);
    PageResult<Rental> findAll(RentalStatus status, int page, int size);
    PageResult<Rental> findOverdue(int page, int size);
}
