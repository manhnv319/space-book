package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CartTest {

    @Test
    @DisplayName("createForUser builds an empty cart with null id")
    void createForUser() {
        Cart cart = Cart.createForUser(7L);
        assertThat(cart.getUserId()).isEqualTo(7L);
        assertThat(cart.getId()).isNull();
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.isGuest()).isFalse();
    }

    @Test
    @DisplayName("createGuest builds an empty guest cart")
    void createGuest() {
        Cart cart = Cart.createGuest();
        assertThat(cart.isGuest()).isTrue();
        assertThat(cart.getUserId()).isNull();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("addItem appends and tracks size")
    void addItem() {
        Cart cart = Cart.createForUser(7L);
        cart.addItem(CartItem.createPurchase(1L, 10L, 1));
        cart.addItem(CartItem.createRental(1L, 11L, 1, "MONTH"));

        assertThat(cart.getItems()).hasSize(2);
        assertThat(cart.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("removeItem drops only matching id")
    void removeItem() {
        Cart cart = Cart.createForUser(7L);
        CartItem kept = CartItem.reconstitute(1L, 1L, 10L, ItemType.PURCHASE, 1, null, null);
        CartItem removed = CartItem.reconstitute(2L, 1L, 11L, ItemType.PURCHASE, 2, null, null);
        cart.addItem(kept);
        cart.addItem(removed);

        cart.removeItem(2L);

        assertThat(cart.getItems()).hasSize(1);
        assertThat(cart.getItems().get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("clear empties the cart")
    void clear() {
        Cart cart = Cart.createForUser(7L);
        cart.addItem(CartItem.createPurchase(1L, 10L, 1));
        cart.clear();
        assertThat(cart.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("CartItem.updateQuantity rejects non-positive values")
    void cartItemUpdateQuantityRejectsNonPositive() {
        CartItem item = CartItem.createPurchase(1L, 10L, 1);
        assertThatThrownBy(() -> item.updateQuantity(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> item.updateQuantity(-3))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
