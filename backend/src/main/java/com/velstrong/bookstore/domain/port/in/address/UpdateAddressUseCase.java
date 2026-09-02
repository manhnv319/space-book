package com.velstrong.bookstore.domain.port.in.address;

import com.velstrong.bookstore.application.command.address.UpdateAddressCommand;
import com.velstrong.bookstore.application.response.address.AddressResponse;

public interface UpdateAddressUseCase {
    AddressResponse update(UpdateAddressCommand command);
}
