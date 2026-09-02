package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.RentalJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.mapper.RentalMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoRentalPersistenceAdapter extends MongoPersistenceSupport implements RentalRepository {

    private static final String COLLECTION = "rentals";
    private final RentalMapper mapper = new RentalMapper();

    public MongoRentalPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Rental save(Rental value) { return mapper.toDomain(save(COLLECTION, mapper.toJpaEntity(value))); }
    @Override public List<Rental> saveAll(List<Rental> values) { return saveAll(COLLECTION, values.stream().map(mapper::toJpaEntity).toList()).stream().map(mapper::toDomain).toList(); }
    @Override public Optional<Rental> findById(Long id) { return findById(COLLECTION, RentalJpaEntity.class, id).map(mapper::toDomain); }
    @Override public boolean existsByOrderItemId(Long orderItemId) { return exists(COLLECTION, Query.query(Criteria.where("orderItemId").is(orderItemId)), RentalJpaEntity.class); }

    @Override public PageResult<Rental> findByUserId(Long userId, RentalStatus status, int page, int size) {
        Criteria criteria = Criteria.where("userId").is(userId);
        if (status != null) criteria = new Criteria().andOperator(criteria, Criteria.where("status").is(status.name()));
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override public PageResult<Rental> findAll(RentalStatus status, int page, int size) {
        Criteria criteria = status == null ? new Criteria() : Criteria.where("status").is(status.name());
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override public PageResult<Rental> findOverdue(int page, int size) {
        Criteria criteria = new Criteria().andOperator(Criteria.where("actualReturnDate").is(null),
                Criteria.where("plannedReturnDate").lt(LocalDate.now()), Criteria.where("status").ne(RentalStatus.RETURNED.name()));
        return toPage(Query.query(criteria).with(Sort.by(Sort.Direction.ASC, "plannedReturnDate")), page, size);
    }

    private PageResult<Rental> toPage(Query query, int page, int size) {
        List<Rental> values = find(COLLECTION, RentalJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(mapper::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), RentalJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }
}
