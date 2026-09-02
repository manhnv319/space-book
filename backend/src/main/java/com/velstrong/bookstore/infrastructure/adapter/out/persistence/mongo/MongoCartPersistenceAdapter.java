package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.Cart;
import com.velstrong.bookstore.domain.port.out.CartRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoCartPersistenceAdapter extends MongoPersistenceSupport implements CartRepository {

    private static final String COLLECTION = "carts";

    public MongoCartPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Cart save(Cart value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<Cart> findByUserId(Long userId) { return findOne(COLLECTION, CartJpaEntity.class, Query.query(Criteria.where("userId").is(userId))).map(this::toDomain); }
    @Override public Optional<Cart> findById(Long id) { return findById(COLLECTION, CartJpaEntity.class, id).map(this::toDomain); }
    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, CartJpaEntity.class); }

    private Cart toDomain(CartJpaEntity e) { return Cart.reconstitute(e.getId(), e.getUserId(), new ArrayList<>()); }

    private CartJpaEntity toEntity(Cart d) {
        CartJpaEntity e = new CartJpaEntity(); e.setId(d.getId()); e.setUserId(d.getUserId()); return e;
    }
}
