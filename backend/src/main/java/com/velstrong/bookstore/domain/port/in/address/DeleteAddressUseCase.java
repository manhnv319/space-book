package com.velstrong.bookstore.domain.port.in.address;

public interface DeleteAddressUseCase {
    void delete(Long addressId, Long userId);
}
