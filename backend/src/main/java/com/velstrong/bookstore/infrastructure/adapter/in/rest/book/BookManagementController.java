package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import com.velstrong.bookstore.application.command.book.UpdateBookFlagsCommand;
import com.velstrong.bookstore.application.response.book.BestsellerSuggestionResponse;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.port.in.book.GetBestsellerSuggestionsUseCase;
import com.velstrong.bookstore.domain.port.in.book.UpdateBookFlagsUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Book curation endpoints for staff (bestseller suggestions + featured/bestseller
 * flags). Resource-oriented under {@code /api/v1/books} per D15 — no {@code /admin}
 * segment; access is restricted purely via the {@code book:manage} permission rule
 * in security-endpoints.yml, registered ahead of the public book routes.
 */
@RestController
@RequestMapping("/api/v1/books")
public class BookManagementController {

    private static final int MAX_LIMIT = 100;
    private static final int MAX_DAYS = 365;

    private final GetBestsellerSuggestionsUseCase getBestsellerSuggestionsUseCase;
    private final UpdateBookFlagsUseCase updateBookFlagsUseCase;

    public BookManagementController(GetBestsellerSuggestionsUseCase getBestsellerSuggestionsUseCase,
                                     UpdateBookFlagsUseCase updateBookFlagsUseCase) {
        this.getBestsellerSuggestionsUseCase = getBestsellerSuggestionsUseCase;
        this.updateBookFlagsUseCase = updateBookFlagsUseCase;
    }

    @GetMapping("/bestseller-suggestions")
    public ResponseEntity<ApiResponse<List<BestsellerSuggestionResponse>>> getBestsellerSuggestions(
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "90") int days,
            @RequestParam(required = false) ItemType itemType) {
        int safeLimit = Math.min(Math.max(limit, 1), MAX_LIMIT);
        int safeDays = Math.min(Math.max(days, 1), MAX_DAYS);
        String itemTypeFilter = itemType != null ? itemType.name() : null;
        return ResponseEntity.ok(ApiResponse.success(
                getBestsellerSuggestionsUseCase.getSuggestions(safeLimit, safeDays, itemTypeFilter)));
    }

    @PutMapping("/{id}/flags")
    public ResponseEntity<ApiResponse<Void>> updateFlags(@PathVariable Long id,
                                                           @Valid @RequestBody UpdateBookFlagsRequest request) {
        updateBookFlagsUseCase.updateFlags(
                new UpdateBookFlagsCommand(id, request.isFeatured(), request.isBestseller()));
        return ResponseEntity.ok(ApiResponse.success("Book flags updated", null));
    }
}
