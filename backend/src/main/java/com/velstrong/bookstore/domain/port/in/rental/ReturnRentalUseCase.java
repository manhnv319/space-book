package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.command.rental.ReturnRentalCommand;
import com.velstrong.bookstore.application.response.rental.RentalResponse;

public interface ReturnRentalUseCase {
    RentalResponse returnBook(ReturnRentalCommand command);
}
