package com.velstrong.bookstore.domain.port.in.cart;

import com.velstrong.bookstore.application.response.cart.CartResponse;

public interface GetCartUseCase {
    CartResponse getByUserId(Long userId);
}
