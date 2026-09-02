package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.port.in.rental.GetOverdueRentalsUseCase;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetOverdueRentalsService implements GetOverdueRentalsUseCase {

    private final RentalRepository rentalRepository;

    public GetOverdueRentalsService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public PagedResponse<RentalResponse> getOverdue(int page, int size) {
        var result = rentalRepository.findOverdue(page, size);
        return PagedResponse.of(
                result.content().stream().map(RentalResponse::from).toList(),
                page, size, result.totalElements());
    }
}
