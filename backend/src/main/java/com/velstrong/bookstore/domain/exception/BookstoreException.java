package com.velstrong.bookstore.domain.exception;

public class BookstoreException extends RuntimeException {

    private final int statusCode;

    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;
    public static final int UNPROCESSABLE = 422;
    public static final int INTERNAL_SERVER_ERROR = 500;

    public BookstoreException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public BookstoreException(String message) {
        super(message);
        this.statusCode = INTERNAL_SERVER_ERROR;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
