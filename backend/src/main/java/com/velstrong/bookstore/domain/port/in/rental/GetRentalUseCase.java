package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;

public interface GetRentalUseCase {
    RentalResponse getById(Long rentalId, Long userId);
}
