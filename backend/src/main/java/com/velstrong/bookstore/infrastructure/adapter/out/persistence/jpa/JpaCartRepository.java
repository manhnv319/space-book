package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaCartRepository extends JpaRepository<CartJpaEntity, Long> {
    Optional<CartJpaEntity> findByUserId(Long userId);
}
