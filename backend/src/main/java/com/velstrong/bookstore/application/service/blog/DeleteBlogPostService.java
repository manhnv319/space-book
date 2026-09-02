package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.domain.port.in.blog.DeleteBlogPostUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeleteBlogPostService implements DeleteBlogPostUseCase {

    private final BlogPostRepository blogPostRepository;

    public DeleteBlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public void delete(Long id) {
        blogPostRepository.deleteById(id);
    }
}
