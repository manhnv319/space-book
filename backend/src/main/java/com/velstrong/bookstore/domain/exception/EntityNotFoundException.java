package com.velstrong.bookstore.domain.exception;

public class EntityNotFoundException extends BookstoreException {

    public EntityNotFoundException(String entityName, Object id) {
        super(entityName + " not found with id: " + id, NOT_FOUND);
    }

    public EntityNotFoundException(String message) {
        super(message, NOT_FOUND);
    }
}
