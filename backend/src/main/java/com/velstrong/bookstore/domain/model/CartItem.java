package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;

public class CartItem {

    private final Long id;
    private final Long cartId;
    private final Long bookId;
    private final ItemType itemType;
    private Integer quantity;
    private Integer rentalTermValue;
    private String rentalTermUnit;

    private CartItem(Long id, Long cartId, Long bookId, ItemType itemType,
                     Integer quantity, Integer rentalTermValue, String rentalTermUnit) {
        this.id = id;
        this.cartId = cartId;
        this.bookId = bookId;
        this.itemType = itemType;
        this.quantity = quantity;
        this.rentalTermValue = rentalTermValue;
        this.rentalTermUnit = rentalTermUnit;
    }

    public static CartItem createPurchase(Long cartId, Long bookId, int quantity) {
        return new CartItem(null, cartId, bookId, ItemType.PURCHASE, quantity, null, null);
    }

    public static CartItem createRental(Long cartId, Long bookId, int rentalTermValue, String rentalTermUnit) {
        return new CartItem(null, cartId, bookId, ItemType.RENTAL, 1, rentalTermValue, rentalTermUnit);
    }

    public static CartItem reconstitute(Long id, Long cartId, Long bookId, ItemType itemType,
                                        Integer quantity, Integer rentalTermValue, String rentalTermUnit) {
        return new CartItem(id, cartId, bookId, itemType, quantity, rentalTermValue, rentalTermUnit);
    }

    public void updateQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");
        this.quantity = quantity;
    }

    public boolean isPurchase() { return ItemType.PURCHASE == itemType; }
    public boolean isRental() { return ItemType.RENTAL == itemType; }

    public Long getId() { return id; }
    public Long getCartId() { return cartId; }
    public Long getBookId() { return bookId; }
    public ItemType getItemType() { return itemType; }
    public Integer getQuantity() { return quantity; }
    public Integer getRentalTermValue() { return rentalTermValue; }
    public String getRentalTermUnit() { return rentalTermUnit; }
}
