package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.BookReview;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.review.ReviewSource;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookReviewJpaEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoBookReviewPersistenceAdapter extends MongoPersistenceSupport implements BookReviewRepository {

    private static final String COLLECTION = "book_reviews";

    public MongoBookReviewPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public BookReview save(BookReview value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public Optional<BookReview> findById(Long id) { return findById(COLLECTION, BookReviewJpaEntity.class, id).map(this::toDomain); }
    @Override public Optional<BookReview> findByUserIdAndOrderItemId(Long userId, Long orderItemId) { return findOne(COLLECTION, BookReviewJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("orderItemId").is(orderItemId)))).map(this::toDomain); }
    @Override public List<BookReview> findByUserIdAndBookId(Long userId, Long bookId) { return find(COLLECTION, BookReviewJpaEntity.class, Query.query(new Criteria().andOperator(Criteria.where("userId").is(userId), Criteria.where("bookId").is(bookId))).with(Sort.by(Sort.Direction.DESC, "createdAt"))).stream().map(this::toDomain).toList(); }

    @Override public PageResult<BookReview> findByBookId(Long bookId, int page, int size) {
        Query query = Query.query(Criteria.where("bookId").is(bookId)).with(Sort.by(Sort.Direction.DESC, "createdAt"));
        List<BookReview> values = find(COLLECTION, BookReviewJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), BookReviewJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    private BookReview toDomain(BookReviewJpaEntity e) { return BookReview.reconstitute(e.getId(), e.getBookId(), e.getUserId(), e.getOrderItemId(), ReviewSource.valueOf(e.getSource()), e.getRating(), e.getComment(), e.getCreatedAt(), e.getModifiedAt()); }
    private BookReviewJpaEntity toEntity(BookReview d) { BookReviewJpaEntity e = new BookReviewJpaEntity(); e.setId(d.getId()); e.setBookId(d.getBookId()); e.setUserId(d.getUserId()); e.setOrderItemId(d.getOrderItemId()); e.setSource(d.getSource().name()); e.setRating((short) d.getRating()); e.setComment(d.getComment()); e.setCreatedAt(d.getCreatedAt()); e.setModifiedAt(d.getModifiedAt()); return e; }
}
