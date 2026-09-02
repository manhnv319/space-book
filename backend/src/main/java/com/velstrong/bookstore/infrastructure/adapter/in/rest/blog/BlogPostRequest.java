package com.velstrong.bookstore.infrastructure.adapter.in.rest.blog;

import com.velstrong.bookstore.application.command.blog.CreateBlogPostCommand;
import com.velstrong.bookstore.application.command.blog.UpdateBlogPostCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record BlogPostRequest(
        @NotBlank String title,
        String slug,
        @Size(max = 500) String excerpt,
        @NotBlank String content,
        @Size(max = 500) String coverImageUrl,
        Long bookId
) {
    public CreateBlogPostCommand toCreateCommand(Long authorId) {
        return new CreateBlogPostCommand(title, slug, excerpt, content, coverImageUrl, bookId, authorId);
    }

    public UpdateBlogPostCommand toUpdateCommand(Long id) {
        return new UpdateBlogPostCommand(id, title, slug, excerpt, content, coverImageUrl, bookId);
    }
}
