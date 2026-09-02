package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GetBlogPostsServiceTest {

    @Test
    void nonManagerAlwaysGetsPublishedOnlyRegardlessOfRequestedStatus() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        BlogPost published = BlogPost.create("slug", "title", "e", "content", null, null, 1L);
        published.publish();
        when(repository.findPublished(0, 20)).thenReturn(PageResult.of(List.of(published), 1));
        GetBlogPostsService service = new GetBlogPostsService(repository);

        // caller explicitly asks for DRAFT via query param — must be ignored
        var response = service.getBlogPosts(BlogPostStatus.DRAFT, false, 0, 20);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).status()).isEqualTo(BlogPostStatus.PUBLISHED);
        verify(repository, never()).findAll(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.anyInt(),
                org.mockito.ArgumentMatchers.anyInt());
    }

    @Test
    void managerCanRequestDraftStatus() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        BlogPost draft = BlogPost.create("slug", "title", "e", "content", null, null, 1L);
        when(repository.findAll(BlogPostStatus.DRAFT, 0, 20)).thenReturn(PageResult.of(List.of(draft), 1));
        GetBlogPostsService service = new GetBlogPostsService(repository);

        var response = service.getBlogPosts(BlogPostStatus.DRAFT, true, 0, 20);

        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).status()).isEqualTo(BlogPostStatus.DRAFT);
    }

    @Test
    void managerWithNoStatusFilterGetsAllStatuses() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        when(repository.findAll(null, 0, 20)).thenReturn(PageResult.of(List.of(), 0));
        GetBlogPostsService service = new GetBlogPostsService(repository);

        service.getBlogPosts(null, true, 0, 20);

        verify(repository).findAll(null, 0, 20);
    }
}
