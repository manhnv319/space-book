package com.velstrong.bookstore.application.service.book;

import com.velstrong.bookstore.application.response.book.BestsellerSuggestionResponse;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.BookSalesCount;
import com.velstrong.bookstore.domain.port.in.book.GetBestsellerSuggestionsUseCase;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GetBestsellerSuggestionsService implements GetBestsellerSuggestionsUseCase {

    private final OrderItemRepository orderItemRepository;
    private final BookRepository bookRepository;

    public GetBestsellerSuggestionsService(OrderItemRepository orderItemRepository,
                                            BookRepository bookRepository) {
        this.orderItemRepository = orderItemRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BestsellerSuggestionResponse> getSuggestions(int limit, int days, String itemType) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<BookSalesCount> sales = orderItemRepository.findTopSellingBooks(since, itemType, limit);
        if (sales.isEmpty()) return List.of();

        List<Long> bookIds = sales.stream().map(BookSalesCount::bookId).toList();
        Map<Long, Book> booksById = bookRepository.findByIds(bookIds).stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        // Keep the sales-descending order from the aggregate query; a book may be
        // missing (e.g. hard-deleted after the sale) and is skipped rather than failing.
        return sales.stream()
                .map(sale -> toResponse(sale, booksById.get(sale.bookId())))
                .filter(Objects::nonNull)
                .toList();
    }

    private BestsellerSuggestionResponse toResponse(BookSalesCount sale, Book book) {
        if (book == null) return null;
        return new BestsellerSuggestionResponse(
                book.getId(), book.getTitle(), sale.soldQuantity(),
                book.getIsFeatured(), book.getIsBestseller());
    }
}
