package com.velstrong.bookstore.application.service.cart;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.response.cart.CartResponse;
import com.velstrong.bookstore.application.response.cart.CartResponse.CartItemDetail;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.in.cart.GetCartUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.domain.port.out.CartRepository;
import com.velstrong.bookstore.domain.service.RentalPricing;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GetCartService implements GetCartUseCase {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public GetCartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                          BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CartResponse getByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cart -> {
                    List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
                    List<CartItemDetail> details = items.stream().map(this::enrich).toList();
                    return CartResponse.of(cart.getId(), cart.getUserId(), details);
                })
                .orElseGet(() -> CartResponse.empty(userId));
    }

    private CartItemDetail enrich(CartItem item) {
        Book book = bookRepository.findById(item.getBookId()).orElse(null);
        String title = book != null ? book.getTitle() : null;

        long unitPrice;
        Long deposit;
        long subtotal;
        if (item.getItemType() == ItemType.RENTAL) {
            unitPrice = book != null
                    ? RentalPricing.rentalFee(book, item.getRentalTermValue(), item.getRentalTermUnit()) : 0L;
            deposit = book != null ? RentalPricing.deposit(book) : 0L;
            subtotal = unitPrice; // rental quantity is always 1
        } else {
            unitPrice = book != null && book.getListPrice() != null ? book.getListPrice() : 0L;
            deposit = null;
            int qty = item.getQuantity() != null ? item.getQuantity() : 0;
            subtotal = unitPrice * qty;
        }

        return new CartItemDetail(item.getId(), item.getBookId(), title, item.getItemType(),
                item.getQuantity(), item.getRentalTermValue(), item.getRentalTermUnit(),
                unitPrice, deposit, subtotal);
    }
}
