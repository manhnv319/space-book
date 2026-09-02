package com.velstrong.bookstore.domain.port.in.address;

import com.velstrong.bookstore.application.command.address.CreateAddressCommand;
import com.velstrong.bookstore.application.response.address.AddressResponse;

public interface CreateAddressUseCase {
    AddressResponse create(CreateAddressCommand command);
}
