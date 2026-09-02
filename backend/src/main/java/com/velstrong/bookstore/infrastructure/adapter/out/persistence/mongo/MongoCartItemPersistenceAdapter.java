package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CartItemJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoCartItemPersistenceAdapter extends MongoPersistenceSupport implements CartItemRepository {

    private static final String COLLECTION = "cart_items";

    public MongoCartItemPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public CartItem save(CartItem value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public List<CartItem> saveAll(List<CartItem> values) { return saveAll(COLLECTION, values.stream().map(this::toEntity).toList()).stream().map(this::toDomain).toList(); }
    @Override public Optional<CartItem> findById(Long id) { return findById(COLLECTION, CartItemJpaEntity.class, id).map(this::toDomain); }
    @Override public List<CartItem> findByCartId(Long cartId) { return find(COLLECTION, CartItemJpaEntity.class, Query.query(Criteria.where("cartId").is(cartId))).stream().map(this::toDomain).toList(); }
    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, CartItemJpaEntity.class); }
    @Override public void deleteByCartId(Long cartId) { mongo.remove(Query.query(Criteria.where("cartId").is(cartId)), COLLECTION); }

    @Override
    public Optional<CartItem> findMatching(Long cartId, Long bookId, ItemType itemType,
                                           Integer rentalTermValue, String rentalTermUnit) {
        Criteria criteria = new Criteria().andOperator(Criteria.where("cartId").is(cartId),
                Criteria.where("bookId").is(bookId), Criteria.where("itemType").is(itemType.name()),
                nullableEquals("rentalTermValue", rentalTermValue), nullableEquals("rentalTermUnit", rentalTermUnit));
        return findOne(COLLECTION, CartItemJpaEntity.class, Query.query(criteria)).map(this::toDomain);
    }

    private Criteria nullableEquals(String field, Object value) {
        return value == null ? Criteria.where(field).is(null) : Criteria.where(field).is(value);
    }

    private CartItem toDomain(CartItemJpaEntity e) {
        return CartItem.reconstitute(e.getId(), e.getCartId(), e.getBookId(),
                e.getItemType() == null ? null : ItemType.valueOf(e.getItemType()), e.getQuantity(),
                e.getRentalTermValue(), e.getRentalTermUnit());
    }

    private CartItemJpaEntity toEntity(CartItem d) {
        CartItemJpaEntity e = new CartItemJpaEntity(); e.setId(d.getId()); e.setCartId(d.getCartId()); e.setBookId(d.getBookId());
        e.setItemType(d.getItemType() == null ? null : d.getItemType().name()); e.setQuantity(d.getQuantity());
        e.setRentalTermValue(d.getRentalTermValue()); e.setRentalTermUnit(d.getRentalTermUnit()); return e;
    }
}
