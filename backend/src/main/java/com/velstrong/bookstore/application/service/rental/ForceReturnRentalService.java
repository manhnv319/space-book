package com.velstrong.bookstore.application.service.rental;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.rental.ForceReturnRentalCommand;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.port.in.rental.ForceReturnRentalUseCase;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.RentalRepository;

import java.time.Clock;
import java.time.LocalDate;


@Service
@Transactional
public class ForceReturnRentalService implements ForceReturnRentalUseCase {

    private final RentalRepository rentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final Clock clock;

    public ForceReturnRentalService(RentalRepository rentalRepository,
                                     BookCopyRepository bookCopyRepository, Clock clock) {
        this.rentalRepository = rentalRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.clock = clock;
    }

    @Override
    public RentalResponse forceReturn(ForceReturnRentalCommand command) {
        Rental rental = rentalRepository.findById(command.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental", command.rentalId()));

        LocalDate today = LocalDate.now(clock);
        rental.returnBook(today);
        if (command.damageFeeAmount() != null && command.damageFeeAmount() > 0)
            rental.applyDamageFee(command.damageFeeAmount());
        if (command.notes() != null)
            rental.setNotes(command.notes());

        BookCopy bookCopy = bookCopyRepository.findById(rental.getBookCopyId())
                .orElseThrow(() -> new EntityNotFoundException("BookCopy", rental.getBookCopyId()));
        bookCopy.markAvailable();
        bookCopyRepository.save(bookCopy);

        return RentalResponse.from(rentalRepository.save(rental));
    }
}
