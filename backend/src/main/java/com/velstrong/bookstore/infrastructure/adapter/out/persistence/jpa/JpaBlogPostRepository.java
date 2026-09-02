package com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BlogPostJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface JpaBlogPostRepository extends JpaRepository<BlogPostJpaEntity, Long> {

    Optional<BlogPostJpaEntity> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<BlogPostJpaEntity> findByStatus(String status, Pageable pageable);

    @Query("SELECT b FROM BlogPostJpaEntity b WHERE (:status IS NULL OR b.status = :status)")
    Page<BlogPostJpaEntity> findAllFiltered(String status, Pageable pageable);
}
