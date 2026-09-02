package com.velstrong.bookstore.application.service.blog;

import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.BlogPost;
import com.velstrong.bookstore.domain.port.out.BlogPostRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetBlogPostBySlugServiceTest {

    @Test
    void nonManagerGetsNotFoundForDraftPost() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        BlogPost draft = BlogPost.create("draft-slug", "title", "e", "content", null, null, 1L);
        when(repository.findBySlug("draft-slug")).thenReturn(Optional.of(draft));
        GetBlogPostBySlugService service = new GetBlogPostBySlugService(repository);

        assertThatThrownBy(() -> service.getBySlug("draft-slug", false))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void managerCanSeeDraftPost() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        BlogPost draft = BlogPost.create("draft-slug", "title", "e", "content", null, null, 1L);
        when(repository.findBySlug("draft-slug")).thenReturn(Optional.of(draft));
        GetBlogPostBySlugService service = new GetBlogPostBySlugService(repository);

        var response = service.getBySlug("draft-slug", true);

        assertThat(response.slug()).isEqualTo("draft-slug");
    }

    @Test
    void nonManagerCanSeePublishedPost() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        BlogPost post = BlogPost.create("live-slug", "title", "e", "content", null, null, 1L);
        post.publish();
        when(repository.findBySlug("live-slug")).thenReturn(Optional.of(post));
        GetBlogPostBySlugService service = new GetBlogPostBySlugService(repository);

        var response = service.getBySlug("live-slug", false);

        assertThat(response.slug()).isEqualTo("live-slug");
    }

    @Test
    void unknownSlugIsNotFound() {
        BlogPostRepository repository = mock(BlogPostRepository.class);
        when(repository.findBySlug("missing")).thenReturn(Optional.empty());
        GetBlogPostBySlugService service = new GetBlogPostBySlugService(repository);

        assertThatThrownBy(() -> service.getBySlug("missing", true))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
