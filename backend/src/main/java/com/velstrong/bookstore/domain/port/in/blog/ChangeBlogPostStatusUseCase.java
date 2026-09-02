package com.velstrong.bookstore.domain.port.in.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostResponse;

public interface ChangeBlogPostStatusUseCase {
    BlogPostResponse publish(Long id);

    BlogPostResponse unpublish(Long id);
}
