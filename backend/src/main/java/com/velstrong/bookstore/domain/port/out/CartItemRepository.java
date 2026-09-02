package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository {
    CartItem save(CartItem cartItem);
    List<CartItem> saveAll(List<CartItem> items);
    Optional<CartItem> findById(Long id);
    List<CartItem> findByCartId(Long cartId);
    void deleteById(Long id);
    void deleteByCartId(Long cartId);

    /** Tìm dòng cart_items trùng để merge. rentalTermValue/rentalTermUnit chỉ có ý nghĩa với RENTAL,
     *  truyền null cho PURCHASE (khi đó match theo cartId+bookId+itemType). */
    Optional<CartItem> findMatching(Long cartId, Long bookId, ItemType itemType,
                                     Integer rentalTermValue, String rentalTermUnit);
}
