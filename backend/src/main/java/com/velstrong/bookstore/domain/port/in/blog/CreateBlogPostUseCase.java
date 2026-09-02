package com.velstrong.bookstore.domain.port.in.blog;

import com.velstrong.bookstore.application.command.blog.CreateBlogPostCommand;
import com.velstrong.bookstore.application.response.blog.BlogPostResponse;

public interface CreateBlogPostUseCase {
    BlogPostResponse create(CreateBlogPostCommand command);
}
