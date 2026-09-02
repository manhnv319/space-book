package com.velstrong.bookstore.application.response.cart;

import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;

import java.util.List;

public record CartResponse(
        Long id,
        Long userId,
        List<CartItemDetail> items,
        Integer totalItems,
        Long totalAmount,
        Long totalDeposit
) {
    public record CartItemDetail(Long id, Long bookId, String bookTitle, ItemType itemType,
                                 Integer quantity, Integer rentalTermValue, String rentalTermUnit,
                                 Long unitPrice, Long depositAmount, Long subtotal) {
        public static CartItemDetail from(CartItem item) {
            return new CartItemDetail(item.getId(), item.getBookId(), null, item.getItemType(),
                    item.getQuantity(), item.getRentalTermValue(), item.getRentalTermUnit(),
                    null, null, null);
        }
    }

    /** Bare response for mutation endpoints (Add/Update/Delete) — FE always re-reads via GET cart. */
    public static CartResponse from(Cart cart) {
        List<CartItemDetail> items = cart.getItems() != null
                ? cart.getItems().stream().map(CartItemDetail::from).toList()
                : List.of();
        return new CartResponse(cart.getId(), cart.getUserId(), items, 0, 0L, 0L);
    }

    /** Enriched response (prices/title/totals) built by GetCartService. */
    public static CartResponse of(Long id, Long userId, List<CartItemDetail> items) {
        int totalItems = items.stream().mapToInt(i -> i.quantity() != null ? i.quantity() : 0).sum();
        long totalAmount = items.stream().mapToLong(i -> i.subtotal() != null ? i.subtotal() : 0L).sum();
        long totalDeposit = items.stream()
                .filter(i -> i.itemType() == ItemType.RENTAL)
                .mapToLong(i -> i.depositAmount() != null ? i.depositAmount() : 0L).sum();
        return new CartResponse(id, userId, items, totalItems, totalAmount, totalDeposit);
    }

    public static CartResponse empty(Long userId) {
        return new CartResponse(null, userId, List.of(), 0, 0L, 0L);
    }
}
