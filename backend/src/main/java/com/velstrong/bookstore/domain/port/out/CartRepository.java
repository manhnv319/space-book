package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.Cart;

import java.util.Optional;

public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findByUserId(Long userId);
    Optional<Cart> findById(Long id);
    void deleteById(Long id);
}
