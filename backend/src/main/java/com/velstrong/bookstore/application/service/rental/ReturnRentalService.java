package com.velstrong.bookstore.application.service.rental;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.rental.ReturnRentalCommand;
import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.port.in.rental.ReturnRentalUseCase;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import com.velstrong.bookstore.domain.model.enums.notification.NotificationType;
import com.velstrong.bookstore.domain.port.in.notification.NotificationUseCase;

import java.time.Clock;
import java.time.LocalDate;


@Service
@Transactional
public class ReturnRentalService implements ReturnRentalUseCase {

    private final RentalRepository rentalRepository;
    private final BookCopyRepository bookCopyRepository;
    private final Clock clock;
    private final NotificationUseCase notifications;

    public ReturnRentalService(RentalRepository rentalRepository, BookCopyRepository bookCopyRepository, Clock clock) {
        this(rentalRepository, bookCopyRepository, clock, null);
    }

    @Autowired
    public ReturnRentalService(RentalRepository rentalRepository, BookCopyRepository bookCopyRepository,
                                 Clock clock, NotificationUseCase notifications) {
        this.rentalRepository = rentalRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.clock = clock;
        this.notifications = notifications;
    }

    @Override
    public RentalResponse returnBook(ReturnRentalCommand command) {
        Rental rental = rentalRepository.findById(command.rentalId())
                .orElseThrow(() -> new EntityNotFoundException("Rental", command.rentalId()));

        if (!rental.getUserId().equals(command.userId()))
            throw new InvalidOperationException("You are not authorized to return this rental");

        LocalDate today = LocalDate.now(clock);
        rental.returnBook(today);
        if (command.damageFeeAmount() != null && command.damageFeeAmount() > 0)
            rental.applyDamageFee(command.damageFeeAmount());

        BookCopy bookCopy = bookCopyRepository.findById(rental.getBookCopyId())
                .orElseThrow(() -> new EntityNotFoundException("BookCopy", rental.getBookCopyId()));
        bookCopy.markAvailable();
        bookCopyRepository.save(bookCopy);

        RentalResponse response = RentalResponse.from(rentalRepository.save(rental));
        if (notifications != null) notifications.notify(rental.getUserId(), NotificationType.RENTAL, "Đã ghi nhận trả sách",
                "Nhà sách đã cập nhật việc trả sách của bạn.", "/account/sach-thue");
        return response;
    }
}
