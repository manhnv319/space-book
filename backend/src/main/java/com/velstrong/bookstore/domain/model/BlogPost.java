package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class BlogPost {

    private static final Pattern SLUG_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

    private final Long id;
    private String slug;
    private String title;
    private String excerpt;
    private String content;
    private String coverImageUrl;
    private BlogPostStatus status;
    private Long bookId;
    private final Long authorId;
    private LocalDateTime publishedAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BlogPost(Long id, String slug, String title, String excerpt, String content,
                     String coverImageUrl, BlogPostStatus status, Long bookId, Long authorId,
                     LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.status = status;
        this.bookId = bookId;
        this.authorId = authorId;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BlogPost create(String slug, String title, String excerpt, String content,
                                  String coverImageUrl, Long bookId, Long authorId) {
        validate(title, content, slug);
        LocalDateTime now = LocalDateTime.now();
        return new BlogPost(null, slug, title, excerpt, content, coverImageUrl,
                BlogPostStatus.DRAFT, bookId, authorId, null, now, now);
    }

    public static BlogPost reconstitute(Long id, String slug, String title, String excerpt, String content,
                                        String coverImageUrl, BlogPostStatus status, Long bookId, Long authorId,
                                        LocalDateTime publishedAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new BlogPost(id, slug, title, excerpt, content, coverImageUrl, status, bookId, authorId,
                publishedAt, createdAt, updatedAt);
    }

    public void update(String slug, String title, String excerpt, String content,
                       String coverImageUrl, Long bookId) {
        validate(title, content, slug);
        this.slug = slug;
        this.title = title;
        this.excerpt = excerpt;
        this.content = content;
        this.coverImageUrl = coverImageUrl;
        this.bookId = bookId;
        this.updatedAt = LocalDateTime.now();
    }

    public void publish() {
        this.status = BlogPostStatus.PUBLISHED;
        if (this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.status = BlogPostStatus.DRAFT;
        this.updatedAt = LocalDateTime.now();
    }

    private static void validate(String title, String content, String slug) {
        if (title == null || title.isBlank()) {
            throw new InvalidOperationException("Blog post title must not be blank");
        }
        if (content == null || content.isBlank()) {
            throw new InvalidOperationException("Blog post content must not be blank");
        }
        if (slug == null || !SLUG_PATTERN.matcher(slug).matches()) {
            throw new InvalidOperationException("Blog post slug is invalid: " + slug);
        }
    }

    public boolean isPublished() {
        return status != null && status.isPublished();
    }

    public Long getId() { return id; }
    public String getSlug() { return slug; }
    public String getTitle() { return title; }
    public String getExcerpt() { return excerpt; }
    public String getContent() { return content; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public BlogPostStatus getStatus() { return status; }
    public Long getBookId() { return bookId; }
    public Long getAuthorId() { return authorId; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
