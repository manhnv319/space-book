package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartItemJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCartItemRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class CartItemPersistenceAdapter implements CartItemRepository {

    private final JpaCartItemRepository jpaRepository;

    public CartItemPersistenceAdapter(JpaCartItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        return toDomain(jpaRepository.save(toJpaEntity(cartItem)));
    }

    @Override
    public List<CartItem> saveAll(List<CartItem> items) {
        return jpaRepository.saveAll(items.stream().map(this::toJpaEntity).toList())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return jpaRepository.findByCartId(cartId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<CartItem> findMatching(Long cartId, Long bookId, ItemType itemType,
                                            Integer rentalTermValue, String rentalTermUnit) {
        return jpaRepository.findMatching(cartId, bookId, itemType.name(), rentalTermValue, rentalTermUnit)
                .map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByCartId(Long cartId) {
        jpaRepository.deleteByCartId(cartId);
    }

    private CartItem toDomain(CartItemJpaEntity e) {
        return CartItem.reconstitute(e.getId(), e.getCartId(), e.getBookId(),
                e.getItemType() != null ? ItemType.valueOf(e.getItemType()) : null,
                e.getQuantity(), e.getRentalTermValue(), e.getRentalTermUnit());
    }

    private CartItemJpaEntity toJpaEntity(CartItem d) {
        CartItemJpaEntity e = new CartItemJpaEntity();
        e.setId(d.getId());
        e.setCartId(d.getCartId());
        e.setBookId(d.getBookId());
        e.setItemType(d.getItemType() != null ? d.getItemType().name() : null);
        e.setQuantity(d.getQuantity());
        e.setRentalTermValue(d.getRentalTermValue());
        e.setRentalTermUnit(d.getRentalTermUnit());
        return e;
    }
}
