package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.command.blog.UpdateBlogPostCommand;
import com.velstrong.bookstore.application.response.blog.BlogPostResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.port.in.blog.UpdateBlogPostUseCase;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateBlogPostService implements UpdateBlogPostUseCase {

    private final BlogPostRepository blogPostRepository;

    public UpdateBlogPostService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    @Override
    public BlogPostResponse update(Long id, UpdateBlogPostCommand command) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("BlogPost", id));

        String base = command.slug() != null && !command.slug().isBlank()
                ? SlugGenerator.generate(command.slug())
                : SlugGenerator.generate(command.title());
        String slug = base.equals(post.getSlug()) ? post.getSlug() : resolveUniqueSlug(base, id);

        post.update(slug, command.title(), command.excerpt(), command.content(),
                command.coverImageUrl(), command.bookId());
        return BlogPostResponse.from(blogPostRepository.save(post));
    }

    private String resolveUniqueSlug(String base, Long excludingId) {
        String candidate = base;
        int suffix = 2;
        while (isTakenByAnotherPost(candidate, excludingId)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private boolean isTakenByAnotherPost(String slug, Long excludingId) {
        return blogPostRepository.findBySlug(slug)
                .map(BlogPost::getId)
                .filter(existingId -> !existingId.equals(excludingId))
                .isPresent();
    }
}
