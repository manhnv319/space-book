package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.command.blog.CreateBlogPostCommand;
import com.velstrong.bookstore.application.response.blog.BlogPostResponse;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.port.in.blog.CreateBlogPostUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateBlogPostService implements CreateBlogPostUseCase {

    private final BlogPostRepository blogPostRepository;

    public CreateBlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public BlogPostResponse create(CreateBlogPostCommand command) {
        String base = command.slug() != null && !command.slug().isBlank()
                ? SlugGenerator.generate(command.slug())
                : SlugGenerator.generate(command.title());
        String slug = resolveUniqueSlug(base);

        BlogPost post = BlogPost.create(slug, command.title(), command.excerpt(), command.content(),
                command.coverImageUrl(), command.bookId(), command.authorId());
        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    private String resolveUniqueSlug(String base) {
        String candidate = base;
        int suffix = 2;
        while (blogPostRepository.existsBySlug(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }
}
