package com.velstrong.bookstore.domain.exception;

public class DuplicateEntityException extends BookstoreException {

    public DuplicateEntityException(String entityName, String field, Object value) {
        super(entityName + " already exists with " + field + ": " + value, CONFLICT);
    }

    public DuplicateEntityException(String message) {
        super(message, CONFLICT);
    }
}
