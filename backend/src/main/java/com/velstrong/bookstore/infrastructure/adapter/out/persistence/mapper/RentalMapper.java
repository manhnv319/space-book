package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper;

import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalJpaEntity;

public class RentalMapper {

    public Rental toDomain(RentalJpaEntity entity) {
        return Rental.reconstitute(
                entity.getId(), entity.getOrderItemId(), entity.getBookCopyId(), entity.getUserId(),
                entity.getRentalTermUnit() != null ? RentalTermUnit.valueOf(entity.getRentalTermUnit()) : null,
                entity.getRentalTermValue(), entity.getDepositAmount(),
                entity.getRentalStartDate(), entity.getPlannedReturnDate(), entity.getActualReturnDate(),
                entity.getStatus() != null ? RentalStatus.valueOf(entity.getStatus()) : null,
                entity.getLateDays(), entity.getLateFeeAmount(), entity.getDamageFeeAmount(),
                entity.getNotes(), entity.getCreatedAt(), entity.getModifiedAt()
        );
    }

    public RentalJpaEntity toJpaEntity(Rental domain) {
        RentalJpaEntity entity = new RentalJpaEntity();
        entity.setId(domain.getId());
        entity.setOrderItemId(domain.getOrderItemId());
        entity.setBookCopyId(domain.getBookCopyId());
        entity.setUserId(domain.getUserId());
        entity.setRentalTermUnit(domain.getRentalTermUnit() != null ? domain.getRentalTermUnit().name() : null);
        entity.setRentalTermValue(domain.getRentalTermValue());
        entity.setDepositAmount(domain.getDepositAmount());
        entity.setRentalStartDate(domain.getRentalStartDate());
        entity.setPlannedReturnDate(domain.getPlannedReturnDate());
        entity.setActualReturnDate(domain.getActualReturnDate());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().name() : null);
        entity.setLateDays(domain.getLateDays());
        entity.setLateFeeAmount(domain.getLateFeeAmount());
        entity.setDamageFeeAmount(domain.getDamageFeeAmount());
        entity.setNotes(domain.getNotes());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setModifiedAt(domain.getModifiedAt());
        return entity;
    }
}
