package com.velstrong.bookstore.infrastructure.adapter.out.persistence.adapter;

import com.velstrong.bookstore.infrastructure.adapter.out.persistence.entity.BookJpaEntity;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaBookRepository;
import com.velstrong.bookstore.infrastructure.adapter.out.persistence.jpa.JpaCategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookPersistenceAdapterTest {

    @Test
    void findByCategoriesUsesCategoryQueryAndMapsCategoryNames() {
        JpaBookRepository bookRepository = mock(JpaBookRepository.class);
        JpaCategoryRepository categoryRepository = mock(JpaCategoryRepository.class);
        BookPersistenceAdapter adapter = new BookPersistenceAdapter(bookRepository, categoryRepository);

        BookJpaEntity book = new BookJpaEntity();
        book.setId(10L);
        book.setIsbn("ISBN");
        book.setTitle("Clean Code");
        book.setIsActive(true);

        when(bookRepository.findByCategoryIds(eq(List.of(1L)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(book)));
        when(categoryRepository.findNamesByBookIds(List.of(10L)))
                .thenReturn(List.<Object[]>of(new Object[]{10L, "Software"}));

        var result = adapter.findByCategories(List.of(1L), 0, 10);

        verify(bookRepository).findByCategoryIds(eq(List.of(1L)), any(Pageable.class));
        verify(bookRepository, never()).findByIsActiveTrue(any(Pageable.class));
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().getFirst().getCategories()).containsExactly("Software");
    }

    @Test
    void findByCategoriesReturnsEmptyWhenCategoryIdsEmpty() {
        JpaBookRepository bookRepository = mock(JpaBookRepository.class);
        JpaCategoryRepository categoryRepository = mock(JpaCategoryRepository.class);
        BookPersistenceAdapter adapter = new BookPersistenceAdapter(bookRepository, categoryRepository);

        var result = adapter.findByCategories(List.of(), 0, 10);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
        verify(bookRepository, never()).findByIsActiveTrue(any(Pageable.class));
        verify(bookRepository, never()).findByCategoryIds(any(), any());
    }
}
