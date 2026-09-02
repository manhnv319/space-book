package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.BookCopy;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository {
    BookCopy save(BookCopy bookCopy);
    Optional<BookCopy> findById(Long id);
    List<BookCopy> findByBookId(Long bookId);
    List<BookCopy> findAvailableByBookId(Long bookId);

    /** Nạp hàng loạt để tránh N+1 khi hiển thị danh sách phiếu thuê. */
    List<BookCopy> findByIds(List<Long> ids);
    Optional<BookCopy> findFirstAvailableByBookIdForUpdate(Long bookId);
    int countAvailableByBookId(Long bookId);
}
