package com.velstrong.bookstore.infrastructure.adapter.in.rest.review;

import com.velstrong.bookstore.application.response.common.PagedResponse;
import com.velstrong.bookstore.application.response.review.BookReviewResponse;
import com.velstrong.bookstore.application.service.review.CreateBookReviewService;
import com.velstrong.bookstore.domain.port.out.BookReviewRepository;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books/{bookId}/reviews")
public class BookReviewController {
    private final BookReviewRepository reviews;
    private final CreateBookReviewService create;
    private final com.velstrong.bookstore.application.service.review.UpdateBookReviewService update;
    public BookReviewController(BookReviewRepository reviews, CreateBookReviewService create, com.velstrong.bookstore.application.service.review.UpdateBookReviewService update) { this.reviews = reviews; this.create = create; this.update = update; }
    @GetMapping public ResponseEntity<ApiResponse<PagedResponse<BookReviewResponse>>> list(@PathVariable Long bookId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page), safeSize = Math.min(50, Math.max(1, size));
        var result = reviews.findByBookId(bookId, safePage, safeSize);
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.of(result.content().stream().map(BookReviewResponse::from).toList(), safePage, safeSize, result.totalElements())));
    }
    @PostMapping public ResponseEntity<ApiResponse<BookReviewResponse>> create(@PathVariable Long bookId, @RequestAttribute Long currentUserId, @Valid @RequestBody BookReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(create.create(request.toCommand(currentUserId, bookId))));
    }
    @PutMapping("/{reviewId}") public ResponseEntity<ApiResponse<BookReviewResponse>> update(@PathVariable Long bookId, @PathVariable Long reviewId, @RequestAttribute Long currentUserId, @Valid @RequestBody BookReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success(update.update(reviewId, request.toCommand(currentUserId, bookId))));
    }
}
