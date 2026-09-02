package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.port.in.rental.GetRentalUseCase;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetRentalService implements GetRentalUseCase {

    private final RentalRepository rentalRepository;
    private final RentalBookLookup bookLookup;

    public GetRentalService(RentalRepository rentalRepository, RentalBookLookup bookLookup) {
        this.rentalRepository = rentalRepository;
        this.bookLookup = bookLookup;
    }

    @Override
    public RentalResponse getById(Long rentalId, Long userId) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new EntityNotFoundException("Rental", rentalId));
        if (!rental.getUserId().equals(userId)) {
            throw new BookstoreException("Rental does not belong to current user", BookstoreException.FORBIDDEN);
        }
        return bookLookup.enrich(rental);
    }
}
