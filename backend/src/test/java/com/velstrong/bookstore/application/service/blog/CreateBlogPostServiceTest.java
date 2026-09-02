package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.application.command.blog.CreateBlogPostCommand;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateBlogPostServiceTest {

    @Test
    void slugifiesTitleWhenSlugNotProvided() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        when(repository.existsBySlug(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreateBlogPostService service = new CreateBlogPostService(repository);

        var response = service.create(new CreateBlogPostCommand(
                "Đọc sách mùa hè", null, "excerpt", "content", null, null, 1L));

        assertThat(response.slug()).isEqualTo("doc-sach-mua-he");
    }

    @Test
    void appendsNumericSuffixWhenSlugAlreadyTaken() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        when(repository.existsBySlug("doc-sach-mua-he")).thenReturn(true);
        when(repository.existsBySlug("doc-sach-mua-he-2")).thenReturn(true);
        when(repository.existsBySlug("doc-sach-mua-he-3")).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreateBlogPostService service = new CreateBlogPostService(repository);

        var response = service.create(new CreateBlogPostCommand(
                "Đọc sách mùa hè", null, "excerpt", "content", null, null, 1L));

        assertThat(response.slug()).isEqualTo("doc-sach-mua-he-3");
    }

    @Test
    void savedPostStartsAsDraft() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        when(repository.existsBySlug(any())).thenReturn(false);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreateBlogPostService service = new CreateBlogPostService(repository);

        var response = service.create(new CreateBlogPostCommand(
                "Title", "custom-slug", null, "content", null, 5L, 1L));

        assertThat(response.slug()).isEqualTo("custom-slug");
        assertThat(response.bookId()).isEqualTo(5L);
    }
}
