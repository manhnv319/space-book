package com.velstrong.bookstore.domain.port.in.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostSummaryResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;

/**
 * Single route serves both guests and admins (D15 rule #5): {@code canManage}
 * is resolved from the caller's authorities and the service enforces the
 * scope — non-managers always get PUBLISHED regardless of {@code statusFilter}.
 */
public interface GetBlogPostsUseCase {
    PagedResponse<BlogPostSummaryResponse> getBlogPosts(BlogPostStatus statusFilter, boolean canManage,
                                                         int page, int size);
}
