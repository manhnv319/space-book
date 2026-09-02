package com.velstrong.bookstore.application.response.book;

import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.enums.book.FormatType;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
        Long id,
        String isbn,
        String title,
        String description,
        String imageUrl,
        FormatType format,
        Long listPrice,
        Long rentalPriceDay,
        Long rentalPriceWeek,
        Long rentalPriceMonth,
        Long depositAmount,
        Short publishYear,
        String publisher,
        String language,
        Short pageCount,
        List<String> authors,
        List<String> categories,
        LocalDateTime createdAt,
        Boolean isFeatured,
        Boolean isBestseller
) {
    public static BookResponse from(Book book) {
        return new BookResponse(
                book.getId(), book.getIsbn(), book.getTitle(), book.getDescription(),
                book.getImageUrl(), book.getFormat(), book.getListPrice(),
                book.getRentalPriceDay(), book.getRentalPriceWeek(), book.getRentalPriceMonth(),
                book.getDepositAmount(), book.getPublishYear(), book.getPublisher(),
                book.getLanguage(), book.getPageCount(), book.getAuthors(), book.getCategories(),
                book.getCreatedAt(), book.getIsFeatured(), book.getIsBestseller()
        );
    }
}
