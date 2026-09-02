package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.port.in.rental.GetAllRentalsUseCase;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetAllRentalsService implements GetAllRentalsUseCase {

    private final RentalRepository rentalRepository;

    public GetAllRentalsService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    @Override
    public PagedResponse<RentalResponse> getAll(RentalStatus status, int page, int size) {
        var result = rentalRepository.findAll(status, page, size);
        return PagedResponse.of(
                result.content().stream().map(RentalResponse::from).toList(),
                page, size, result.totalElements());
    }
}
