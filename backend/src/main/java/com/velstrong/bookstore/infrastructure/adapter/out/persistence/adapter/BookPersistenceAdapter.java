package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaBookRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class BookPersistenceAdapter implements BookRepository {

    private final JpaBookRepository jpaBookRepository;
    private final JpaCategoryRepository jpaCategoryRepository;

    public BookPersistenceAdapter(JpaBookRepository jpaBookRepository,
                                  JpaCategoryRepository jpaCategoryRepository) {
        this.jpaBookRepository = jpaBookRepository;
        this.jpaCategoryRepository = jpaCategoryRepository;
    }

    @Override
    public Optional<Book> findById(Long id) {
        return jpaBookRepository.findById(id)
                .map(entity -> toDomain(entity, jpaCategoryRepository.findNamesByBookId(entity.getId())));
    }

    @Override
    public PageResult<Book> findAll(int page, int size, String sortBy, boolean asc) {
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Page<BookJpaEntity> result = jpaBookRepository.findByIsActiveTrue(PageRequest.of(page, size, sort));
        return PageResult.of(toDomainList(result.toList()), result.getTotalElements());
    }

    @Override
    public PageResult<Book> findByCategories(List<Long> categoryIds, int page, int size) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return PageResult.of(List.of(), 0);
        }
        Page<BookJpaEntity> result = jpaBookRepository.findByCategoryIds(categoryIds, PageRequest.of(page, size));
        return PageResult.of(toDomainList(result.toList()), result.getTotalElements());
    }

    @Override
    public PageResult<Book> searchByTitle(String keyword, int page, int size) {
        Page<BookJpaEntity> result = jpaBookRepository.searchByTitle(keyword, PageRequest.of(page, size));
        return PageResult.of(toDomainList(result.toList()), result.getTotalElements());
    }

    @Override
    public PageResult<Book> findByShelf(BookShelf shelf, int page, int size) {
        Sort sort = shelf == BookShelf.NEW_ARRIVAL
                ? Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
                : Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        Page<BookJpaEntity> result = switch (shelf) {
            case FEATURED -> jpaBookRepository.findFeatured(pageRequest);
            case BESTSELLER -> jpaBookRepository.findBestsellers(pageRequest);
            case NEW_ARRIVAL -> jpaBookRepository.findByIsActiveTrue(pageRequest);
        };
        return PageResult.of(toDomainList(result.toList()), result.getTotalElements());
    }

    @Override
    public List<Book> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        return toDomainList(jpaBookRepository.findByIdIn(ids));
    }

    @Override
    public boolean updateFlags(Long id, boolean isFeatured, boolean isBestseller) {
        return jpaBookRepository.updateFlags(id, isFeatured, isBestseller) > 0;
    }

    @Override
    public boolean updateImageUrl(Long id, String imageUrl) { return jpaBookRepository.updateImageUrl(id, imageUrl) > 0; }

    private List<Book> toDomainList(List<BookJpaEntity> entities) {
        Map<Long, List<String>> categoriesByBook = loadCategories(entities);
        return entities.stream()
                .map(entity -> toDomain(entity, categoriesByBook.getOrDefault(entity.getId(), List.of())))
                .toList();
    }

    private Map<Long, List<String>> loadCategories(List<BookJpaEntity> entities) {
        List<Long> bookIds = entities.stream().map(BookJpaEntity::getId).toList();
        if (bookIds.isEmpty()) return Map.of();

        Map<Long, List<String>> result = new LinkedHashMap<>();
        for (Object[] row : jpaCategoryRepository.findNamesByBookIds(bookIds)) {
            Long bookId = ((Number) row[0]).longValue();
            String categoryName = (String) row[1];
            result.computeIfAbsent(bookId, ignored -> new ArrayList<>()).add(categoryName);
        }
        return result;
    }

    private Book toDomain(BookJpaEntity e, List<String> categories) {
        return Book.reconstitute(e.getId(), e.getIsbn(), e.getTitle(), e.getDescription(),
                e.getImageUrl(), e.getFormat() != null ? FormatType.valueOf(e.getFormat()) : null,
                e.getListPrice(), e.getRentalPriceDay(), e.getRentalPriceWeek(),
                e.getRentalPriceMonth(), e.getDepositAmount(), e.getPublishYear(),
                e.getPublisher(), e.getLanguage(), e.getPageCount(), e.getIsActive(),
                List.of(), categories, e.getCreatedAt(), e.getIsFeatured(), e.getIsBestseller());
    }
}
