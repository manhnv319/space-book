package com.velstrong.bookstore.infrastructure.adapter.out.persistence.mongo;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BlogPostJpaEntity;
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
public class MongoBlogPostPersistenceAdapter extends MongoPersistenceSupport implements BlogPostRepository {

    private static final String COLLECTION = "blog_posts";

    public MongoBlogPostPersistenceAdapter(MongoTemplate mongo) { super(mongo); }

    @Override public Optional<BlogPost> findById(Long id) { return findById(COLLECTION, BlogPostJpaEntity.class, id).map(this::toDomain); }
    @Override public Optional<BlogPost> findBySlug(String slug) { return findOne(COLLECTION, BlogPostJpaEntity.class, Query.query(Criteria.where("slug").is(slug))).map(this::toDomain); }
    @Override public boolean existsBySlug(String slug) { return exists(COLLECTION, Query.query(Criteria.where("slug").is(slug)), BlogPostJpaEntity.class); }

    @Override public PageResult<BlogPost> findPublished(int page, int size) {
        return toPage(Query.query(Criteria.where("status").is(BlogPostStatus.PUBLISHED.name())).with(Sort.by(Sort.Direction.DESC, "publishedAt")), page, size);
    }

    @Override public PageResult<BlogPost> findAll(BlogPostStatus status, int page, int size) {
        Query query = new Query(); if (status != null) query.addCriteria(Criteria.where("status").is(status.name()));
        return toPage(query.with(Sort.by(Sort.Direction.DESC, "createdAt")), page, size);
    }

    @Override public BlogPost save(BlogPost value) { return toDomain(save(COLLECTION, toEntity(value))); }
    @Override public void deleteById(Long id) { deleteById(COLLECTION, id, BlogPostJpaEntity.class); }

    private PageResult<BlogPost> toPage(Query query, int page, int size) {
        List<BlogPost> values = find(COLLECTION, BlogPostJpaEntity.class, query.limit(size).skip((long) page * size)).stream().map(this::toDomain).toList();
        long total = mongo.count(Query.of(query).limit(-1).skip(-1), BlogPostJpaEntity.class, COLLECTION);
        return PageResult.of(values, total);
    }

    private BlogPost toDomain(BlogPostJpaEntity e) {
        return BlogPost.reconstitute(e.getId(), e.getSlug(), e.getTitle(), e.getExcerpt(), e.getContent(), e.getCoverImageUrl(),
                BlogPostStatus.valueOf(e.getStatus()), e.getBookId(), e.getAuthorId(), e.getPublishedAt(), e.getCreatedAt(), e.getUpdatedAt());
    }

    private BlogPostJpaEntity toEntity(BlogPost d) {
        BlogPostJpaEntity e = new BlogPostJpaEntity(); e.setId(d.getId()); e.setSlug(d.getSlug()); e.setTitle(d.getTitle()); e.setExcerpt(d.getExcerpt());
        e.setContent(d.getContent()); e.setCoverImageUrl(d.getCoverImageUrl()); e.setStatus(d.getStatus().name()); e.setBookId(d.getBookId());
        e.setAuthorId(d.getAuthorId()); e.setPublishedAt(d.getPublishedAt()); e.setCreatedAt(d.getCreatedAt()); e.setUpdatedAt(d.getUpdatedAt()); return e;
    }
}
