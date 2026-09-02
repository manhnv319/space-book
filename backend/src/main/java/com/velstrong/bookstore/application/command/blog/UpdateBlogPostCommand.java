package com.velstrong.bookstore.application.command.blog;

public record UpdateBlogPostCommand(
        Long id,
        String title,
        String slug,
        String excerpt,
        String content,
        String coverImageUrl,
        Long bookId
) {
}
