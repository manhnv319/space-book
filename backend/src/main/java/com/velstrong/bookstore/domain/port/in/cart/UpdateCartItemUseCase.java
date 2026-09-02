package com.velstrong.bookstore.domain.port.in.cart;

import com.velstrong.bookstore.application.command.cart.UpdateCartItemCommand;
import com.velstrong.bookstore.application.response.cart.CartResponse;

public interface UpdateCartItemUseCase {
    CartResponse updateItem(UpdateCartItemCommand command);
}
