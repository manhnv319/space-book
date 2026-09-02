package com.velstrong.bookstore.domain.exception;

public class InsufficientStockException extends BookstoreException {

    public InsufficientStockException(Long bookId, int requested, int available) {
        super("Insufficient stock for book " + bookId + ": requested " + requested + ", available " + available, UNPROCESSABLE);
    }

    public InsufficientStockException(String message) {
        super(message, UNPROCESSABLE);
    }
}
