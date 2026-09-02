package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.port.in.rental.GetMyRentalsUseCase;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetMyRentalsService implements GetMyRentalsUseCase {

    private final RentalRepository rentalRepository;
    private final RentalBookLookup bookLookup;

    public GetMyRentalsService(RentalRepository rentalRepository, RentalBookLookup bookLookup) {
        this.rentalRepository = rentalRepository;
        this.bookLookup = bookLookup;
    }

    @Override
    public PagedResponse<RentalResponse> getMyRentals(Long userId, RentalStatus status, int page, int size) {
        var result = rentalRepository.findByUserId(userId, status, page, size);
        return PagedResponse.of(bookLookup.enrich(result.content()), page, size, result.totalElements());
    }
}
