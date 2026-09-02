package com.velstrong.bookstore.infrastructure.adapter.in.rest.review;

import com.velstrong.bookstore.application.response.review.ReviewTransactionResponse;
import com.velstrong.bookstore.application.service.review.GetMyBookReviewOptionsService;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews/books/{bookId}/me")
public class MyBookReviewController {
    private final GetMyBookReviewOptionsService options;
    public MyBookReviewController(GetMyBookReviewOptionsService options) { this.options = options; }
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<ReviewTransactionResponse>>> get(@org.springframework.web.bind.annotation.PathVariable Long bookId,
                                                                                        @RequestAttribute Long currentUserId) {
        return ResponseEntity.ok(ApiResponse.success(options.get(currentUserId, bookId)));
    }
}
