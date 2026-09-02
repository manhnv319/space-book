package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaBookRepository extends JpaRepository<BookJpaEntity, Long> {
    Page<BookJpaEntity> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT b FROM BookJpaEntity b WHERE b.isActive = true AND b.title LIKE %:keyword%")
    Page<BookJpaEntity> searchByTitle(String keyword, Pageable pageable);

    @Query("""
            SELECT DISTINCT b FROM BookJpaEntity b
            JOIN BookCategoryJpaEntity bc ON bc.bookId = b.id
            WHERE b.isActive = true
              AND bc.categoryId IN :categoryIds
            """)
    Page<BookJpaEntity> findByCategoryIds(List<Long> categoryIds, Pageable pageable);

    @Query("SELECT b FROM BookJpaEntity b WHERE b.isActive = true AND b.isFeatured = true")
    Page<BookJpaEntity> findFeatured(Pageable pageable);

    @Query("SELECT b FROM BookJpaEntity b WHERE b.isActive = true AND b.isBestseller = true")
    Page<BookJpaEntity> findBestsellers(Pageable pageable);

    List<BookJpaEntity> findByIdIn(List<Long> ids);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE BookJpaEntity b SET b.isFeatured = :isFeatured, b.isBestseller = :isBestseller WHERE b.id = :id")
    int updateFlags(Long id, boolean isFeatured, boolean isBestseller);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE BookJpaEntity b SET b.imageUrl = :imageUrl WHERE b.id = :id")
    int updateImageUrl(Long id, String imageUrl);
}
