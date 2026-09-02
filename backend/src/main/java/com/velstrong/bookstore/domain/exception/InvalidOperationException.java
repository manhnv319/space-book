package com.velstrong.bookstore.domain.exception;

public class InvalidOperationException extends BookstoreException {

    public InvalidOperationException(String message) {
        super(message, BAD_REQUEST);
    }
}
