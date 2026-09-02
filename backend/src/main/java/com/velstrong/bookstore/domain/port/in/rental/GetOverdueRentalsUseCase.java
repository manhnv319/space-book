package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.rental.RentalResponse;

public interface GetOverdueRentalsUseCase {
    PagedResponse<RentalResponse> getOverdue(int page, int size);
}
