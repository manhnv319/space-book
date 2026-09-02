package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.port.in.blog.GetBlogPostBySlugUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetBlogPostBySlugService implements GetBlogPostBySlugUseCase {

    private final BlogPostRepository blogPostRepository;

    public GetBlogPostBySlugService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public BlogPostResponse getBySlug(String slug, boolean canManage) {
        // 404 (not 403) for a non-PUBLISHED post seen by a non-manager — avoids
        // revealing that a draft with this slug exists.
        return blogPostRepository.findBySlug(slug)
                .filter(post -> canManage || post.isPublished())
                .map(BlogPostResponse::from)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost", slug));
    }
}
