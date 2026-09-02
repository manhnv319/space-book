package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;

public class OrderItem {

    private final Long id;
    private Long orderId;
    private final Long bookId;
    private final Long bookCopyId;
    private final ItemType itemType;
    private final Integer quantity;
    private final Long unitPrice;
    private final Long depositAmount;
    private final Integer rentalTermValue;
    private final RentalTermUnit rentalTermUnit;
    private Long subtotal;

    private OrderItem(Long id, Long orderId, Long bookId, Long bookCopyId, ItemType itemType, Integer quantity,
                      Long unitPrice, Long depositAmount, Integer rentalTermValue, RentalTermUnit rentalTermUnit,
                      Long subtotal) {
        this.id = id;
        this.orderId = orderId;
        this.bookId = bookId;
        this.bookCopyId = bookCopyId;
        this.itemType = itemType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.depositAmount = depositAmount;
        this.rentalTermValue = rentalTermValue;
        this.rentalTermUnit = rentalTermUnit;
        this.subtotal = subtotal;
    }

    public static OrderItem createPurchase(Long bookId, int quantity, long unitPrice) {
        return new OrderItem(null, null, bookId, null, ItemType.PURCHASE, quantity, unitPrice, 0L,
                null, null, (long) quantity * unitPrice);
    }

    public static OrderItem createRental(Long bookId, Long bookCopyId, long unitPrice, long depositAmount,
                                         int termValue, RentalTermUnit termUnit) {
        return new OrderItem(null, null, bookId, bookCopyId, ItemType.RENTAL, 1, unitPrice, depositAmount,
                termValue, termUnit, unitPrice);
    }

    public static OrderItem createRental(Long bookId, Long bookCopyId, long unitPrice, long depositAmount) {
        return createRental(bookId, bookCopyId, unitPrice, depositAmount, 1, RentalTermUnit.MONTH);
    }

    public static OrderItem reconstitute(Long id, Long orderId, Long bookId, Long bookCopyId, ItemType itemType,
                                         Integer quantity, Long unitPrice, Long depositAmount,
                                         Integer rentalTermValue, RentalTermUnit rentalTermUnit, Long subtotal) {
        return new OrderItem(id, orderId, bookId, bookCopyId, itemType, quantity, unitPrice, depositAmount,
                rentalTermValue, rentalTermUnit, subtotal);
    }

    public void calculateSubtotal() {
        subtotal = (quantity != null ? quantity : 0) * (unitPrice != null ? unitPrice : 0L);
    }

    public boolean isPurchase() { return ItemType.PURCHASE == itemType; }
    public boolean isRental() { return ItemType.RENTAL == itemType; }
    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public Long getBookId() { return bookId; }
    public Long getBookCopyId() { return bookCopyId; }
    public ItemType getItemType() { return itemType; }
    public Integer getQuantity() { return quantity; }
    public Long getUnitPrice() { return unitPrice; }
    public Long getDepositAmount() { return depositAmount; }
    public Integer getRentalTermValue() { return rentalTermValue; }
    public RentalTermUnit getRentalTermUnit() { return rentalTermUnit; }
    public Long getSubtotal() { return subtotal; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
}
