package com.velstrong.bookstore.application.service.cart;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.cart.DeleteCartItemCommand;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.port.in.cart.DeleteCartItemUseCase;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;


@Service
@Transactional
public class DeleteCartItemService implements DeleteCartItemUseCase {

    private final CartItemRepository cartItemRepository;

    public DeleteCartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public void deleteItem(DeleteCartItemCommand command) {
        CartItem item = cartItemRepository.findById(command.cartItemId())
                .orElseThrow(() -> new EntityNotFoundException("CartItem", command.cartItemId()));
        cartItemRepository.deleteById(command.cartItemId());
    }
}
