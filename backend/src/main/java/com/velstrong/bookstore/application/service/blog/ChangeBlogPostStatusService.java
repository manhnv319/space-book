package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.response.blog.BlogPostResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.port.in.blog.ChangeBlogPostStatusUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChangeBlogPostStatusService implements ChangeBlogPostStatusUseCase {

    private final BlogPostRepository blogPostRepository;

    public ChangeBlogPostStatusService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public BlogPostResponse publish(Long id) {
        BlogPost post = findOrThrow(id);
        post.publish();
        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    @Override
    public BlogPostResponse unpublish(Long id) {
        BlogPost post = findOrThrow(id);
        post.unpublish();
        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    private BlogPost findOrThrow(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost", id));
    }
}
