package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.port.out.CartRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCartRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class CartPersistenceAdapter implements CartRepository {

    private final JpaCartRepository jpaRepository;

    public CartPersistenceAdapter(JpaCartRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Cart save(Cart cart) {
        return toDomain(jpaRepository.save(toJpaEntity(cart)));
    }

    @Override
    public Optional<Cart> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId).map(this::toDomain);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Cart toDomain(CartJpaEntity e) {
        return Cart.reconstitute(e.getId(), e.getUserId(), new ArrayList<>());
    }

    private CartJpaEntity toJpaEntity(Cart d) {
        CartJpaEntity e = new CartJpaEntity();
        e.setId(d.getId());
        e.setUserId(d.getUserId());
        return e;
    }
}
