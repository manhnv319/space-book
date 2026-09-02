package com.velstrong.bookstore.domain.port.in.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostResponse;

/**
 * Single route serves both guests and admins (D15 rule #5). Non-managers
 * requesting a non-PUBLISHED slug get a 404 (via EntityNotFoundException),
 * not a 403 — this avoids leaking the existence of a draft.
 */
public interface GetBlogPostBySlugUseCase {
    BlogPostResponse getBySlug(String slug, boolean canManage);
}
