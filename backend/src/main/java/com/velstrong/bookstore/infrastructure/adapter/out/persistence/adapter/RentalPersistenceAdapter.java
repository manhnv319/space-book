package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaRentalRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.RentalMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class RentalPersistenceAdapter implements RentalRepository {

    private final JpaRentalRepository jpaRepository;
    private final RentalMapper mapper;

    public RentalPersistenceAdapter(JpaRentalRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
        this.mapper = new RentalMapper();
    }

    @Override
    public Rental save(Rental rental) {
        return mapper.toDomain(jpaRepository.save(mapper.toJpaEntity(rental)));
    }

    @Override
    public List<Rental> saveAll(List<Rental> rentals) {
        return jpaRepository.saveAll(rentals.stream().map(mapper::toJpaEntity).toList())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Rental> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByOrderItemId(Long orderItemId) {
        return jpaRepository.existsByOrderItemId(orderItemId);
    }

    @Override
    public PageResult<Rental> findByUserId(Long userId, RentalStatus status, int page, int size) {
        String statusStr = status != null ? status.name() : null;
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RentalJpaEntity> result = jpaRepository.findByUserIdWithStatus(userId, statusStr, pageable);
        return PageResult.of(result.map(mapper::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public PageResult<Rental> findAll(RentalStatus status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<RentalJpaEntity> result = jpaRepository.findAllWithStatus(
                status != null ? status.name() : null, pageable);
        return PageResult.of(result.map(mapper::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public PageResult<Rental> findOverdue(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<RentalJpaEntity> result = jpaRepository.findOverdue(LocalDate.now(), pageable);
        return PageResult.of(result.map(mapper::toDomain).toList(), result.getTotalElements());
    }
}
