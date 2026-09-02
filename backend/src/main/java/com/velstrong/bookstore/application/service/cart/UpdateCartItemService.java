package com.velstrong.bookstore.application.service.cart;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.cart.UpdateCartItemCommand;
import com.velstrong.bookstore.application.response.cart.CartResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.port.in.cart.UpdateCartItemUseCase;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.domain.port.out.CartRepository;


@Service
@Transactional
public class UpdateCartItemService implements UpdateCartItemUseCase {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public UpdateCartItemService(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    public CartResponse updateItem(UpdateCartItemCommand command) {
        CartItem item = cartItemRepository.findById(command.cartItemId())
                .orElseThrow(() -> new EntityNotFoundException("CartItem", command.cartItemId()));
        item.updateQuantity(command.quantity());
        cartItemRepository.save(item);

        Cart cart = cartRepository.findByUserId(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Cart for user", command.userId()));
        cart.setItems(cartItemRepository.findByCartId(cart.getId()));
        return CartResponse.from(cart);
    }
}
