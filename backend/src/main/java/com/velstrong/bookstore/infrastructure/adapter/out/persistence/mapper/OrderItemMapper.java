package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper;

import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.OrderItemJpaEntity;

public class OrderItemMapper {

    public OrderItem toDomain(OrderItemJpaEntity entity) {
        RentalTermUnit termUnit = entity.getRentalTermUnit() == null ? null
                : RentalTermUnit.valueOf(entity.getRentalTermUnit());
        return OrderItem.reconstitute(entity.getId(), entity.getOrderId(), entity.getBookId(), entity.getBookCopyId(),
                ItemType.valueOf(entity.getItemType()), entity.getQuantity(), entity.getUnitPrice(),
                entity.getDepositAmount(), entity.getRentalTermValue(), termUnit, entity.getSubtotal());
    }

    public OrderItemJpaEntity toJpaEntity(OrderItem domain) {
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
