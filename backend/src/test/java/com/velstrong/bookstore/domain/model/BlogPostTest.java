package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.blog.BlogPostStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlogPostTest {

    @Test
    void createStartsAsDraftWithoutPublishedAt() {
        BlogPost post = BlogPost.create("hello-world", "Hello world", "excerpt",
                "content", null, null, 1L);

        assertThat(post.getStatus()).isEqualTo(BlogPostStatus.DRAFT);
        assertThat(post.getPublishedAt()).isNull();
        assertThat(post.isPublished()).isFalse();
    }

    @Test
    void createRejectsBlankTitle() {
        assertThatThrownBy(() -> BlogPost.create("slug", "  ", "e", "content", null, null, 1L))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void createRejectsBlankContent() {
        assertThatThrownBy(() -> BlogPost.create("slug", "title", "e", "  ", null, null, 1L))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void createRejectsInvalidSlugFormat() {
        assertThatThrownBy(() -> BlogPost.create("Invalid Slug!", "title", "e", "content", null, null, 1L))
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    void publishSetsStatusAndPublishedAtOnce() {
        BlogPost post = BlogPost.create("slug", "title", "e", "content", null, null, 1L);

        post.publish();
        var firstPublishedAt = post.getPublishedAt();
        assertThat(post.getStatus()).isEqualTo(BlogPostStatus.PUBLISHED);
        assertThat(firstPublishedAt).isNotNull();

        post.unpublish();
        post.publish();

        // re-publishing keeps the original publishedAt (doesn't reset the "first published" timestamp)
        assertThat(post.getPublishedAt()).isEqualTo(firstPublishedAt);
    }

    @Test
    void unpublishRevertsToDraftButKeepsPublishedAt() {
        BlogPost post = BlogPost.create("slug", "title", "e", "content", null, null, 1L);
        post.publish();
        var publishedAt = post.getPublishedAt();

        post.unpublish();

        assertThat(post.getStatus()).isEqualTo(BlogPostStatus.DRAFT);
        assertThat(post.getPublishedAt()).isEqualTo(publishedAt);
    }

    @Test
    void updateRevalidatesFields() {
        BlogPost post = BlogPost.create("slug", "title", "e", "content", null, null, 1L);

        assertThatThrownBy(() -> post.update("slug", "", "e", "content", null, null))
                .isInstanceOf(InvalidOperationException.class);
    }
}
