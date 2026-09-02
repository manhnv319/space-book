package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import com.velstrong.bookstore.application.response.book.BookResponse;
import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.domain.model.enums.book.BookShelf;
import com.velstrong.bookstore.domain.port.in.book.GetAllBooksUseCase;
import com.velstrong.bookstore.domain.port.in.book.GetBookShelfUseCase;
import com.velstrong.bookstore.domain.port.in.book.GetBookUseCase;
import com.velstrong.bookstore.domain.port.in.book.GetBooksByCategoryUseCase;
import com.velstrong.bookstore.domain.port.in.book.SearchBooksUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {

    private final GetAllBooksUseCase getAllBooksUseCase;
    private final GetBookUseCase getBookUseCase;
    private final SearchBooksUseCase searchBooksUseCase;
    private final GetBooksByCategoryUseCase getBooksByCategoryUseCase;
    private final GetBookShelfUseCase getBookShelfUseCase;

    public BookController(GetAllBooksUseCase getAllBooksUseCase, GetBookUseCase getBookUseCase,
                          SearchBooksUseCase searchBooksUseCase,
                          GetBooksByCategoryUseCase getBooksByCategoryUseCase,
                          GetBookShelfUseCase getBookShelfUseCase) {
        this.getAllBooksUseCase = getAllBooksUseCase;
        this.getBookUseCase = getBookUseCase;
        this.searchBooksUseCase = searchBooksUseCase;
        this.getBooksByCategoryUseCase = getBooksByCategoryUseCase;
        this.getBookShelfUseCase = getBookShelfUseCase;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "true") boolean asc) {
        return ResponseEntity.ok(ApiResponse.success(getAllBooksUseCase.getAll(page, size, sortBy, asc)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(getBookUseCase.getById(id)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(searchBooksUseCase.search(keyword, page, size)));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getByCategories(
            @RequestParam List<Long> categoryIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getBooksByCategoryUseCase.getByCategories(categoryIds, page, size)));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getFeatured(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getBookShelfUseCase.getShelf(BookShelf.FEATURED, page, size)));
    }

    @GetMapping("/bestsellers")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getBestsellers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getBookShelfUseCase.getShelf(BookShelf.BESTSELLER, page, size)));
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getNewArrivals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
                getBookShelfUseCase.getShelf(BookShelf.NEW_ARRIVAL, page, size)));
    }
}
