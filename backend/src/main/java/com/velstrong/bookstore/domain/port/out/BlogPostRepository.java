package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;

import java.util.Optional;

public interface BlogPostRepository {
    Optional<BlogPost> findById(Long id);

    Optional<BlogPost> findBySlug(String slug);

    boolean existsBySlug(String slug);

    /** Published-only listing, sorted by publishedAt DESC (uses idx_blog_posts_status_published_at). */
    PageResult<BlogPost> findPublished(int page, int size);

    /** Admin listing across statuses. Null status means "all statuses", sorted by createdAt DESC. */
    PageResult<BlogPost> findAll(BlogPostStatus status, int page, int size);

    BlogPost save(BlogPost post);

    void deleteById(Long id);
}
