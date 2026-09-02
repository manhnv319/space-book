package com.velstrong.bookstore.domain.port.in.address;

import com.velstrong.bookstore.application.response.address.AddressResponse;

import java.util.List;

public interface GetAllAddressesUseCase {
    List<AddressResponse> getAllByUserId(Long userId);
}
