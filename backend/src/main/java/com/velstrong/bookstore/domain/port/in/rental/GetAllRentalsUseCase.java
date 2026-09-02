package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;

public interface GetAllRentalsUseCase {
    PagedResponse<RentalResponse> getAll(RentalStatus status, int page, int size);
}
