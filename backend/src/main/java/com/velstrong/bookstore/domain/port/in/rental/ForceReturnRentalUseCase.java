package com.velstrong.bookstore.domain.port.in.rental;

import com.velstrong.bookstore.application.command.rental.ForceReturnRentalCommand;
import com.velstrong.bookstore.application.response.rental.RentalResponse;

public interface ForceReturnRentalUseCase {
    RentalResponse forceReturn(ForceReturnRentalCommand command);
}
