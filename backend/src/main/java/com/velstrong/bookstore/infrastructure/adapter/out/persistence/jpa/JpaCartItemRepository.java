package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaCartItemRepository extends JpaRepository<CartItemJpaEntity, Long> {
    List<CartItemJpaEntity> findByCartId(Long cartId);
    void deleteByCartId(Long cartId);

    @Query("""
            SELECT ci FROM CartItemJpaEntity ci
            WHERE ci.cartId = :cartId
              AND ci.bookId = :bookId
              AND ci.itemType = :itemType
              AND ((:rentalTermValue IS NULL AND ci.rentalTermValue IS NULL) OR ci.rentalTermValue = :rentalTermValue)
              AND ((:rentalTermUnit IS NULL AND ci.rentalTermUnit IS NULL) OR ci.rentalTermUnit = :rentalTermUnit)
            """)
    Optional<CartItemJpaEntity> findMatching(Long cartId, Long bookId, String itemType,
                                              Integer rentalTermValue, String rentalTermUnit);
}
