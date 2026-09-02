package com.velstrong.bookstore.application.service.blog;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SlugGeneratorTest {

    @Test
    void stripsVietnameseDiacriticsAndPunctuation() {
        String slug = SlugGenerator.generate("Đọc sách mùa hè & những điều thú vị");

        assertThat(slug).isEqualTo("doc-sach-mua-he-nhung-dieu-thu-vi");
    }

    @Test
    void collapsesConsecutiveSeparatorsAndTrimsDashes() {
        assertThat(SlugGenerator.generate("  Hello   World!!  ")).isEqualTo("hello-world");
    }

    @Test
    void lowercasesPlainAsciiInput() {
        assertThat(SlugGenerator.generate("My Awesome Post")).isEqualTo("my-awesome-post");
    }

    @Test
    void truncatesToMaxLengthWithoutTrailingDash() {
        String longTitle = "a".repeat(210) + " b";

        String slug = SlugGenerator.generate(longTitle);

        assertThat(slug.length()).isLessThanOrEqualTo(200);
        assertThat(slug).doesNotEndWith("-");
    }

    @Test
    void returnsEmptyStringForNullInput() {
        assertThat(SlugGenerator.generate(null)).isEmpty();
    }
}
