package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Optional<Book> findById(Long id);
    PageResult<Book> findAll(int page, int size, String sortBy, boolean asc);
    PageResult<Book> findByCategories(List<Long> categoryIds, int page, int size);
    PageResult<Book> searchByTitle(String keyword, int page, int size);
    PageResult<Book> findByShelf(BookShelf shelf, int page, int size);
    List<Book> findByIds(List<Long> ids);
    boolean updateFlags(Long id, boolean isFeatured, boolean isBestseller);
    boolean updateImageUrl(Long id, String imageUrl);
}
