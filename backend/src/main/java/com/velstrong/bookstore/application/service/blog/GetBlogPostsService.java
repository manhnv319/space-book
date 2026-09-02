package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostSummaryResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import com.velstrong.bookstore.domain.port.in.blog.GetBlogPostsUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetBlogPostsService implements GetBlogPostsUseCase {

    private final BlogPostRepository blogPostRepository;

    public GetBlogPostsService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public PagedResponse<BlogPostSummaryResponse> getBlogPosts(BlogPostStatus statusFilter, boolean canManage,
                                                                int page, int size) {
        // Non-managers never see anything but PUBLISHED, no matter what statusFilter asks for.
        boolean forcePublishedOnly = !canManage || statusFilter == BlogPostStatus.PUBLISHED;
        PageResult<BlogPost> result = forcePublishedOnly
                ? blogPostRepository.findPublished(page, size)
                : blogPostRepository.findAll(statusFilter, page, size);

        return PagedResponse.of(
                result.content().stream().map(BlogPostSummaryResponse::from).toList(),
                page, size, result.totalElements());
    }
}
