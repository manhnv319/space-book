package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.UserAddress;

import java.util.List;
import java.util.Optional;

public interface UserAddressRepository {
    UserAddress save(UserAddress address);
    Optional<UserAddress> findById(Long id);
    List<UserAddress> findByUserId(Long userId);
    void deleteById(Long id);
}
