package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gắn tên sách vào phiếu thuê.
 *
 * Phiếu thuê chỉ trỏ tới một bản sao, còn tên sách nằm cách đó hai bảng. Nạp
 * theo lô hai lần — bản sao rồi đầu sách — thay vì tra từng phiếu, nên một trang
 * phiếu thuê tốn đúng hai truy vấn chứ không phải hai truy vấn mỗi dòng.
 *
 * Bản sao hoặc đầu sách đã bị xoá thì trả về phiếu với tên rỗng, không loại nó
 * khỏi danh sách: người thuê vẫn cần thấy phiếu và khoản cọc của mình.
 */
@Component
public class RentalBookLookup {

    private final BookCopyRepository bookCopies;
    private final BookRepository books;

    public RentalBookLookup(BookCopyRepository bookCopies, BookRepository books) {
        this.bookCopies = bookCopies;
        this.books = books;
    }

    public List<RentalResponse> enrich(List<Rental> rentals) {
        if (rentals.isEmpty()) return List.of();

        List<Long> copyIds = rentals.stream().map(Rental::getBookCopyId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, BookCopy> copies = bookCopies.findByIds(copyIds).stream()
                .collect(Collectors.toMap(BookCopy::getId, Function.identity(), (a, b) -> a));

        List<Long> bookIds = copies.values().stream().map(BookCopy::getBookId).filter(java.util.Objects::nonNull).distinct().toList();
        Map<Long, Book> booksById = books.findByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity(), (a, b) -> a));

        return rentals.stream().map(rental -> {
            BookCopy copy = rental.getBookCopyId() == null ? null : copies.get(rental.getBookCopyId());
            Book book = copy == null || copy.getBookId() == null ? null : booksById.get(copy.getBookId());
            return RentalResponse.from(rental, book == null ? null : book.getId(),
                    book == null ? null : book.getTitle());
        }).toList();
    }

    public RentalResponse enrich(Rental rental) {
        return enrich(List.of(rental)).getFirst();
    }
}
