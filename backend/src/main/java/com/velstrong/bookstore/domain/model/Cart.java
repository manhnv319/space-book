package com.velstrong.bookstore.domain.model;

import java.util.ArrayList;
import java.util.List;

public class Cart {

    private final Long id;
    private final Long userId;
    private List<CartItem> items;

    private Cart(Long id, Long userId, List<CartItem> items) {
        this.id = id;
        this.userId = userId;
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
    }

    public static Cart createForUser(Long userId) {
        return new Cart(null, userId, new ArrayList<>());
    }

    public static Cart createGuest() {
        return new Cart(null, null, new ArrayList<>());
    }

    public static Cart reconstitute(Long id, Long userId, List<CartItem> items) {
        return new Cart(id, userId, items);
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public void removeItem(Long cartItemId) {
        items.removeIf(i -> cartItemId.equals(i.getId()));
    }

    public void clear() {
        items.clear();
    }

    public boolean isGuest() { return userId == null; }
    public boolean isEmpty() { return items == null || items.isEmpty(); }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }
}
