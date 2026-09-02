package com.velstrong.bookstore.domain.model.enums.book;

/**
 * Homepage shelves backed by dedicated queries (Phase 02). Kept in the domain
 * so the mapping "which shelf uses which query" is not leaked as a free-form
 * string in the REST layer.
 */
public enum BookShelf {
    FEATURED,
    BESTSELLER,
    NEW_ARRIVAL
}
