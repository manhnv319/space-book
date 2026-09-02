package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.CategoryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface JpaCategoryRepository extends JpaRepository<CategoryJpaEntity, Long> {

    List<CategoryJpaEntity> findAllByOrderByNameAsc();

    @Query("""
            SELECT c.name FROM CategoryJpaEntity c
            JOIN BookCategoryJpaEntity bc ON bc.categoryId = c.id
            WHERE bc.bookId = :bookId
            ORDER BY c.name
            """)
    List<String> findNamesByBookId(Long bookId);

    @Query("""
            SELECT bc.bookId, c.name FROM CategoryJpaEntity c
            JOIN BookCategoryJpaEntity bc ON bc.categoryId = c.id
            WHERE bc.bookId IN :bookIds
            ORDER BY c.name
            """)
    List<Object[]> findNamesByBookIds(Collection<Long> bookIds);
}
