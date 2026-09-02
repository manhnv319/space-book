package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import org.bson.Document;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("mongodb & !postgres")
public class MongoBookPersistenceAdapter extends MongoPersistenceSupport implements BookRepository {

    private static final String BOOKS = "books";
    private static final String CATEGORIES = "categories";
    private static final String BOOK_CATEGORIES = "book_categories";

    public MongoBookPersistenceAdapter(MongoTemplate mongo) {
        super(mongo);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return findById(BOOKS, BookJpaEntity.class, id)
                .map(entity -> toDomain(entity, loadCategoryNames(entity.getId())));
    }

    @Override
    public PageResult<Book> findAll(int page, int size, String sortBy, boolean asc) {
        Query query = Query.query(Criteria.where("isActive").is(true))
                .with(Sort.by(asc ? Sort.Direction.ASC : Sort.Direction.DESC, safeSort(sortBy)));
        return toPage(query, page, size);
    }

    @Override
    public PageResult<Book> findByCategories(List<Long> categoryIds, int page, int size) {
        if (categoryIds == null || categoryIds.isEmpty()) return PageResult.of(List.of(), 0);
        List<Long> bookIds = mongo.find(Query.query(Criteria.where("categoryId").in(categoryIds)),
                        Document.class, BOOK_CATEGORIES).stream()
                .map(value -> ((Number) value.get("bookId")).longValue())
                .distinct().toList();
        if (bookIds.isEmpty()) return PageResult.of(List.of(), 0);
        return toPage(Query.query(new Criteria().andOperator(
                Criteria.where("isActive").is(true), Criteria.where("_id").in(bookIds))), page, size);
    }

    @Override
    public PageResult<Book> searchByTitle(String keyword, int page, int size) {
        Criteria criteria = Criteria.where("isActive").is(true);
        if (keyword != null && !keyword.isBlank()) {
            criteria = new Criteria().andOperator(criteria,
                    Criteria.where("title").regex(java.util.regex.Pattern.quote(keyword.trim()), "i"));
        }
        return toPage(Query.query(criteria), page, size);
    }

    @Override
    public PageResult<Book> findByShelf(BookShelf shelf, int page, int size) {
        Criteria criteria = Criteria.where("isActive").is(true);
        if (shelf == BookShelf.FEATURED) criteria = new Criteria().andOperator(criteria, Criteria.where("isFeatured").is(true));
        if (shelf == BookShelf.BESTSELLER) criteria = new Criteria().andOperator(criteria, Criteria.where("isBestseller").is(true));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdAt", "_id"));
        return toPage(query, page, size);
    }

    @Override
    public List<Book> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return toDomainList(find(BOOKS, BookJpaEntity.class, Query.query(Criteria.where("_id").in(ids))));
    }

    @Override
    public boolean updateFlags(Long id, boolean isFeatured, boolean isBestseller) {
        return updateFirst(BOOKS, Query.query(Criteria.where("_id").is(id)),
                new Update().set("isFeatured", isFeatured).set("isBestseller", isBestseller), BookJpaEntity.class)
                .getMatchedCount() > 0;
    }

    @Override
    public boolean updateImageUrl(Long id, String imageUrl) {
        return updateFirst(BOOKS, Query.query(Criteria.where("_id").is(id)),
                new Update().set("imageUrl", imageUrl), BookJpaEntity.class).getMatchedCount() > 0;
    }

    private PageResult<Book> toPage(Query query, int page, int size) {
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), BookJpaEntity.class, BOOKS);
        List<BookJpaEntity> entities = find(BOOKS, BookJpaEntity.class,
                query.limit(size).skip((long) page * size));
        return PageResult.of(toDomainList(entities), total);
    }

    private List<Book> toDomainList(List<BookJpaEntity> entities) {
        Map<Long, List<String>> categories = loadCategories(entities);
        return entities.stream().map(entity -> toDomain(entity,
                categories.getOrDefault(entity.getId(), List.of()))).toList();
    }

    private Map<Long, List<String>> loadCategories(List<BookJpaEntity> books) {
        if (books.isEmpty()) return Map.of();
        Map<Long, List<String>> result = new LinkedHashMap<>();
        books.forEach(book -> result.put(book.getId(), loadCategoryNames(book.getId())));
        return result;
    }

    private List<String> loadCategoryNames(Long bookId) {
        List<Long> categoryIds = mongo.find(Query.query(Criteria.where("bookId").is(bookId)),
                        Document.class, BOOK_CATEGORIES).stream()
                .map(value -> ((Number) value.get("categoryId")).longValue()).toList();
        if (categoryIds.isEmpty()) return List.of();
        return mongo.find(Query.query(Criteria.where("_id").in(categoryIds)), CategoryJpaEntity.class, CATEGORIES)
                .stream().sorted(java.util.Comparator.comparing(CategoryJpaEntity::getName,
                        java.util.Comparator.nullsLast(String::compareToIgnoreCase)))
                .map(CategoryJpaEntity::getName).toList();
    }

    private Book toDomain(BookJpaEntity entity, List<String> categories) {
        return Book.reconstitute(entity.getId(), entity.getIsbn(), entity.getTitle(), entity.getDescription(),
                entity.getImageUrl(), entity.getFormat() != null ? FormatType.valueOf(entity.getFormat()) : null,
                entity.getListPrice(), entity.getRentalPriceDay(), entity.getRentalPriceWeek(),
                entity.getRentalPriceMonth(), entity.getDepositAmount(), entity.getPublishYear(),
                entity.getPublisher(), entity.getLanguage(), entity.getPageCount(), entity.getIsActive(),
                List.of(), categories, entity.getCreatedAt(), entity.getIsFeatured(), entity.getIsBestseller());
    }

    private String safeSort(String sortBy) {
        return switch (sortBy == null ? "createdAt" : sortBy) {
            case "title", "createdAt", "listPrice", "rentalPriceDay" -> sortBy;
            default -> "createdAt";
        };
    }
}
