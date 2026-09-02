package com.velstrong.bookstore.application.command.blog;

public record CreateBlogPostCommand(
        String title,
        String slug,
        String excerpt,
        String content,
        String coverImageUrl,
        Long bookId,
        Long authorId
) {
}
