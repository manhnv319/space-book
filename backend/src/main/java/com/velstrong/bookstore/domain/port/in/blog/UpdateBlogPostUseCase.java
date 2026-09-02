package com.velstrong.bookstore.domain.port.in.blog;

import com.velstrong.bookstore.application.command.blog.UpdateBlogPostCommand;
import com.velstrong.bookstore.application.response.blog.BlogPostResponse;

public interface UpdateBlogPostUseCase {
    BlogPostResponse update(Long id, UpdateBlogPostCommand command);
}
