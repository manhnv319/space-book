package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.order.PaymentMethod;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderJpaEntity;

import java.util.List;

public class OrderMapper {

    public Order toDomain(OrderJpaEntity entity) {
        return Order.reconstitute(entity.getId(), entity.getUserId(), entity.getOrderCode(),
                OrderType.valueOf(entity.getOrderType()), OrderStatus.valueOf(entity.getStatus()),
                PaymentStatus.valueOf(entity.getPaymentStatus()), PaymentMethod.valueOf(entity.getPaymentMethod()),
                entity.getTotalItems(), entity.getTotalAmount(), entity.getTotalDeposit(), entity.getTotalDiscount(),
                entity.getVoucherId(), entity.getShippingAddressId(), entity.getNotes(), entity.getCreatedAt(),
                entity.getModifiedAt(), List.of());
    }

    /** Cập nhật lên entity đang được quản lý — xem ghi chú ở OrderPersistenceAdapter.save. */
    public OrderJpaEntity applyTo(OrderJpaEntity entity, Order domain) {
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setOrderCode(domain.getOrderCode());
        entity.setOrderType(domain.getOrderType().name());
        entity.setStatus(domain.getStatus().name());
        entity.setPaymentStatus(domain.getPaymentStatus().name());
        entity.setPaymentMethod(domain.getPaymentMethod().name());
        entity.setTotalItems(domain.getTotalItems());
        entity.setTotalAmount(domain.getTotalAmount());
        entity.setTotalDeposit(domain.getTotalDeposit());
        entity.setTotalDiscount(domain.getTotalDiscount());
        entity.setVoucherId(domain.getVoucherId());
        entity.setShippingAddressId(domain.getShippingAddressId());
        entity.setNotes(domain.getNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setModifiedAt(domain.getModifiedAt());
        return entity;
    }

    public OrderItem toItemDomain(OrderItemJpaEntity entity) {
        RentalTermUnit termUnit = entity.getRentalTermUnit() == null ? null
                : RentalTermUnit.valueOf(entity.getRentalTermUnit());
        return OrderItem.reconstitute(entity.getId(), entity.getOrderId(), entity.getBookId(), entity.getBookCopyId(),
                ItemType.valueOf(entity.getItemType()), entity.getQuantity(), entity.getUnitPrice(),
                entity.getDepositAmount(), entity.getRentalTermValue(), termUnit, entity.getSubtotal());
    }

    public OrderItemJpaEntity toItemJpaEntity(OrderItem domain) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderId(domain.getOrderId());
        entity.setBookId(domain.getBookId());
        entity.setBookCopyId(domain.getBookCopyId());
        entity.setItemType(domain.getItemType().name());
        entity.setQuantity(domain.getQuantity());
        entity.setUnitPrice(domain.getUnitPrice());
        entity.setDepositAmount(domain.getDepositAmount());
        entity.setRentalTermValue(domain.getRentalTermValue());
        entity.setRentalTermUnit(domain.getRentalTermUnit() != null ? domain.getRentalTermUnit().name() : null);
        entity.setSubtotal(domain.getSubtotal());
        return entity;
    }
}
