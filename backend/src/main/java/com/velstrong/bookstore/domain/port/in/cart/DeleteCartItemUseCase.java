package com.velstrong.bookstore.domain.port.in.cart;

import com.velstrong.bookstore.application.command.cart.DeleteCartItemCommand;

public interface DeleteCartItemUseCase {
    void deleteItem(DeleteCartItemCommand command);
}
