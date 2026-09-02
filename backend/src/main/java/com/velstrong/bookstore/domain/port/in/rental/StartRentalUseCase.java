package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;

import java.util.List;

public interface StartRentalUseCase {
    List<RentalResponse> startFromOrder(Long orderId);
}
