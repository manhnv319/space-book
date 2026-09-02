package com.velstrong.bookstore.infrastructure.adapter.in.rest.book;

import com.velstrong.bookstore.application.command.book.CreateBookCopyCommand;
import com.velstrong.bookstore.application.command.book.UpdateBookCopyCommand;
import com.velstrong.bookstore.application.response.book.BookCopyResponse;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;
import com.velstrong.bookstore.domain.port.in.book.ManageBookCopiesUseCase;
import com.velstrong.bookstore.infrastructure.adapter.in.rest.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookCopyManagementController {
    private final ManageBookCopiesUseCase manageCopies;

    public BookCopyManagementController(ManageBookCopiesUseCase manageCopies) {
        this.manageCopies = manageCopies;
    }

    @GetMapping("/books/{bookId}/copies")
    public ResponseEntity<ApiResponse<List<BookCopyResponse>>> getByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(ApiResponse.success(manageCopies.getByBookId(bookId)));
    }

    @PostMapping("/books/{bookId}/copies")
    public ResponseEntity<ApiResponse<BookCopyResponse>> create(@PathVariable Long bookId,
                                                                  @Valid @RequestBody CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(
                manageCopies.create(new CreateBookCopyCommand(bookId, request.condition()))));
    }

    @PatchMapping("/book-copies/{copyId}")
    public ResponseEntity<ApiResponse<BookCopyResponse>> update(@PathVariable Long copyId,
                                                                  @Valid @RequestBody UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(manageCopies.update(
                new UpdateBookCopyCommand(copyId, request.status(), request.condition(), request.notes()))));
    }

    public record CreateRequest(@NotNull BookCopyCondition condition) {}
    public record UpdateRequest(@NotNull BookCopyStatus status, @NotNull BookCopyCondition condition, String notes) {}
}
