package com.velstrong.bookstore.domain.port.in.cart;

import com.velstrong.bookstore.application.command.cart.AddCartItemCommand;
import com.velstrong.bookstore.application.response.cart.CartResponse;

public interface AddCartItemUseCase {
    CartResponse addItem(AddCartItemCommand command);
}
