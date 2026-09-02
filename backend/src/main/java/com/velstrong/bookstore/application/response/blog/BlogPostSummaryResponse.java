package com.velstrong.bookstore.application.response.blog;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;

import java.time.LocalDateTime;

/** List view — deliberately excludes {@code content} to avoid shipping full bodies to list pages. */
public record BlogPostSummaryResponse(
        Long id,
        String slug,
        String title,
        String excerpt,
        String coverImageUrl,
        Long bookId,
        BlogPostStatus status,
        LocalDateTime publishedAt
) {
    public static BlogPostSummaryResponse from(BlogPost post) {
        return new BlogPostSummaryResponse(
                post.getId(), post.getSlug(), post.getTitle(), post.getExcerpt(),
                post.getCoverImageUrl(), post.getBookId(), post.getStatus(), post.getPublishedAt()
        );
    }
}
