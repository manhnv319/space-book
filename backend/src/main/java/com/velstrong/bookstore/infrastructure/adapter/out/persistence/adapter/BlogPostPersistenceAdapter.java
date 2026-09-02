package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BlogPostJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaBlogPostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("postgres & !mongodb")
public class BlogPostPersistenceAdapter implements BlogPostRepository {

    private final JpaBlogPostRepository jpaRepository;

    public BlogPostPersistenceAdapter(JpaBlogPostRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<BlogPost> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<BlogPost> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public PageResult<BlogPost> findPublished(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "publishedAt"));
        Page<BlogPostJpaEntity> result = jpaRepository.findByStatus(BlogPostStatus.PUBLISHED.name(), pageRequest);
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public PageResult<BlogPost> findAll(BlogPostStatus status, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogPostJpaEntity> result = jpaRepository.findAllFiltered(
                status != null ? status.name() : null, pageRequest);
        return PageResult.of(result.map(this::toDomain).toList(), result.getTotalElements());
    }

    @Override
    public BlogPost save(BlogPost post) {
        return toDomain(jpaRepository.save(toEntity(post)));
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private BlogPost toDomain(BlogPostJpaEntity e) {
        return BlogPost.reconstitute(e.getId(), e.getSlug(), e.getTitle(), e.getExcerpt(), e.getContent(),
                e.getCoverImageUrl(), BlogPostStatus.valueOf(e.getStatus()), e.getBookId(), e.getAuthorId(),
                e.getPublishedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    private BlogPostJpaEntity toEntity(BlogPost d) {
        BlogPostJpaEntity e = new BlogPostJpaEntity();
        e.setId(d.getId());
        e.setSlug(d.getSlug());
        e.setTitle(d.getTitle());
        e.setExcerpt(d.getExcerpt());
        e.setContent(d.getContent());
        e.setCoverImageUrl(d.getCoverImageUrl());
        e.setStatus(d.getStatus().name());
        e.setBookId(d.getBookId());
        e.setAuthorId(d.getAuthorId());
        e.setPublishedAt(d.getPublishedAt());
        e.setCreatedAt(d.getCreatedAt());
        e.setUpdatedAt(d.getUpdatedAt());
        return e;
    }
}
