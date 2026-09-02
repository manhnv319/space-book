package com.velstrong.bookstore.application.response.blog;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;

import java.time.LocalDateTime;

public record BlogPostResponse(
        Long id,
        String slug,
        String title,
        String excerpt,
        String content,
        String coverImageUrl,
        BlogPostStatus status,
        Long bookId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static BlogPostResponse from(BlogPost post) {
        return new BlogPostResponse(
                post.getId(), post.getSlug(), post.getTitle(), post.getExcerpt(), post.getContent(),
                post.getCoverImageUrl(), post.getStatus(), post.getBookId(),
                post.getPublishedAt(), post.getCreatedAt(), post.getUpdatedAt()
        );
    }
}
