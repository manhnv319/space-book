package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;

public class BookCopy {

    private final Long id;
    private final Long bookId;
    private BookCopyStatus status;
    private BookCopyCondition condition;
    private String notes;

    private BookCopy(Long id, Long bookId, BookCopyStatus status,
                     BookCopyCondition condition, String notes) {
        this.id = id;
        this.bookId = bookId;
        this.status = status;
        this.condition = condition;
        this.notes = notes;
    }

    public static BookCopy create(Long bookId, BookCopyCondition condition) {
        return new BookCopy(null, bookId, BookCopyStatus.AVAILABLE, condition, null);
    }

    public static BookCopy reconstitute(Long id, Long bookId, BookCopyStatus status,
                                        BookCopyCondition condition, String notes) {
        return new BookCopy(id, bookId, status, condition, notes);
    }

    public void markRented() {
        if (!status.isAvailable()) throw new InvalidOperationException("BookCopy is not available, current status: " + status);
        this.status = BookCopyStatus.RENTED;
    }

    public void markAvailable() {
        this.status = BookCopyStatus.AVAILABLE;
    }

    public void markDamaged(String notes) {
        this.status = BookCopyStatus.DAMAGED;
        this.notes = notes;
    }

    public void markLost() { this.status = BookCopyStatus.LOST; }

    public void updateManagement(BookCopyStatus status, BookCopyCondition condition, String notes) {
        if (status == null || condition == null) {
            throw new InvalidOperationException("Book copy status and condition are required");
        }
        this.status = status;
        this.condition = condition;
        this.notes = notes == null || notes.isBlank() ? null : notes.trim();
    }

    public boolean isAvailable() { return status != null && status.isAvailable(); }

    public Long getId() { return id; }
    public Long getBookId() { return bookId; }
    public BookCopyStatus getStatus() { return status; }
    public BookCopyCondition getCondition() { return condition; }
    public String getNotes() { return notes; }
}
