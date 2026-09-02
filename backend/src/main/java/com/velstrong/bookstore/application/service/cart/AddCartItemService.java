package com.velstrong.bookstore.application.service.cart;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.cart.AddCartItemCommand;
import com.velstrong.bookstore.application.response.cart.CartResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.in.cart.AddCartItemUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.domain.port.out.CartRepository;

import java.util.Optional;

@Service
@Transactional
public class AddCartItemService implements AddCartItemUseCase {

    private static final int MAX_QUANTITY = 99;

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;

    public AddCartItemService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                               BookRepository bookRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public CartResponse addItem(AddCartItemCommand command) {
        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book", command.bookId()));
        validateAvailability(book, command.itemType());

        Cart cart = cartRepository.findByUserId(command.userId())
                .orElseGet(() -> createCartSafely(command.userId()));

        CartItem saved = mergeOrCreate(cart, command);
        cart.addItem(saved);

        return CartResponse.from(cart);
    }

    private void validateAvailability(Book book, ItemType itemType) {
        boolean available = ItemType.RENTAL == itemType
                ? book.isAvailableForRental()
                : book.isAvailableForSale();
        if (!available) {
            String reason = ItemType.RENTAL == itemType ? "rental" : "sale";
            throw new InvalidOperationException("Book is not available for " + reason + ": " + book.getId());
        }
    }

    /**
     * Chống race condition tạo cart: 2 request đồng thời của cùng user chỉ tạo đúng 1 row.
     * Cần unique constraint {@code uk_carts_user_id} (V10) để catch bên dưới có tác dụng —
     * request thua cuộc nhận DataIntegrityViolationException rồi đọc lại cart do request kia vừa tạo.
     */
    private Cart createCartSafely(Long userId) {
        try {
            return cartRepository.save(Cart.createForUser(userId));
        } catch (DataIntegrityViolationException e) {
            return cartRepository.findByUserId(userId).orElseThrow(() -> e);
        }
    }

    /**
     * Merge item trùng: PURCHASE cùng bookId cộng dồn quantity (cap {@link #MAX_QUANTITY}).
     * RENTAL cùng bookId + cùng (rentalTermValue, rentalTermUnit) giữ nguyên (idempotent, không cộng dồn
     * — "thuê 2 lần cùng kỳ hạn" không có ý nghĩa nghiệp vụ rõ ràng). RENTAL khác kỳ hạn tạo dòng mới.
     */
    private CartItem mergeOrCreate(Cart cart, AddCartItemCommand command) {
        boolean isRental = ItemType.RENTAL == command.itemType();
        Integer termValue = isRental ? command.rentalTermValue() : null;
        String termUnit = isRental ? command.rentalTermUnit() : null;

        Optional<CartItem> existing = cartItemRepository.findMatching(cart.getId(), command.bookId(),
                command.itemType(), termValue, termUnit);

        if (existing.isPresent()) {
            CartItem item = existing.get();
            if (item.isPurchase()) {
                int newQuantity = Math.min(item.getQuantity() + command.quantity(), MAX_QUANTITY);
                item.updateQuantity(newQuantity);
                return cartItemRepository.save(item);
            }
            return item;
        }

        CartItem item = isRental
                ? CartItem.createRental(cart.getId(), command.bookId(), command.rentalTermValue(), command.rentalTermUnit())
                : CartItem.createPurchase(cart.getId(), command.bookId(), command.quantity());
        return cartItemRepository.save(item);
    }
}
