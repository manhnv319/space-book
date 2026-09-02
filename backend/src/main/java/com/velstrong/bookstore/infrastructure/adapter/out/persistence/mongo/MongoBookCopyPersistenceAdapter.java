package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookCopyJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Profile("mongodb & !postgres")
public class MongoBookCopyPersistenceAdapter extends MongoPersistenceSupport implements BookCopyRepository {

    private static final String COLLECTION = "book_copies";

    public MongoBookCopyPersistenceAdapter(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public BookCopy save(BookCopy value) {
        return toDomain(save(COLLECTION, toEntity(value)));
    }

    @Override
    public Optional<BookCopy> findById(Long id) {
        return findById(COLLECTION, BookCopyJpaEntity.class, id).map(this::toDomain);
    }

    @Override
    public List<BookCopy> findByBookId(Long bookId) {
        return find(COLLECTION, BookCopyJpaEntity.class,
                Query.query(Criteria.where("bookId").is(bookId)).with(Sort.by(Sort.Direction.ASC, "_id")))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<BookCopy> findAvailableByBookId(Long bookId) {
        return find(COLLECTION, BookCopyJpaEntity.class,
                Query.query(new Criteria().andOperator(Criteria.where("bookId").is(bookId),
                        Criteria.where("status").is(BookCopyStatus.AVAILABLE.name()))))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<BookCopy> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return find(COLLECTION, BookCopyJpaEntity.class, Query.query(Criteria.where("_id").in(ids)))
                .stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BookCopy> findFirstAvailableByBookIdForUpdate(Long bookId) {
        Query query = Query.query(new Criteria().andOperator(Criteria.where("bookId").is(bookId),
                Criteria.where("status").is(BookCopyStatus.AVAILABLE.name())))
                .with(Sort.by(Sort.Direction.ASC, "_id")).limit(1);
        // A write inside the Mongo transaction gives this document a lock conflict
        // with competing checkout transactions, matching the JPA FOR UPDATE intent.
        return findAndModify(COLLECTION, BookCopyJpaEntity.class, query,
                new Update().set("_mongoLock", UUID.randomUUID().toString())).map(this::toDomain);
    }

    @Override
    public int countAvailableByBookId(Long bookId) {
        return (int) count(COLLECTION, Query.query(new Criteria().andOperator(
                Criteria.where("bookId").is(bookId), Criteria.where("status").is(BookCopyStatus.AVAILABLE.name()))),
                BookCopyJpaEntity.class);
    }

    private BookCopy toDomain(BookCopyJpaEntity entity) {
        return BookCopy.reconstitute(entity.getId(), entity.getBookId(),
                entity.getStatus() == null ? null : BookCopyStatus.valueOf(entity.getStatus()),
                entity.getCondition() == null ? null : BookCopyCondition.valueOf(entity.getCondition()), entity.getNotes());
    }

    private BookCopyJpaEntity toEntity(BookCopy value) {
        BookCopyJpaEntity entity = new BookCopyJpaEntity();
        entity.setId(value.getId());
        entity.setBookId(value.getBookId());
        entity.setStatus(value.getStatus() == null ? null : value.getStatus().name());
        entity.setCondition(value.getCondition() == null ? null : value.getCondition().name());
        entity.setNotes(value.getNotes());
        return entity;
    }
}
