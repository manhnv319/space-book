package com.velstrong.bookstore.domain.model;

import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyCondition;
import com.velstrong.bookstore.domain.model.enums.book.BookCopyStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookCopyTest {

    @Test
    @DisplayName("create produces an AVAILABLE copy")
    void create() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        assertThat(copy.getId()).isNull();
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
        assertThat(copy.getCondition()).isEqualTo(BookCopyCondition.NEW);
        assertThat(copy.isAvailable()).isTrue();
    }

    @Test
    @DisplayName("markRented moves AVAILABLE -> RENTED")
    void markRented() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.markRented();
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.RENTED);
        assertThat(copy.isAvailable()).isFalse();
    }

    @Test
    @DisplayName("markRented rejects when not AVAILABLE")
    void markRentedRejectsFromNonAvailable() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.markRented();
        assertThatThrownBy(copy::markRented)
                .isInstanceOf(InvalidOperationException.class);
    }

    @Test
    @DisplayName("markAvailable resets to AVAILABLE regardless of current state")
    void markAvailable() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.markRented();
        copy.markAvailable();
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.AVAILABLE);
    }

    @Test
    @DisplayName("markDamaged sets status and notes")
    void markDamaged() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.markDamaged("broken spine");
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.DAMAGED);
        assertThat(copy.getNotes()).isEqualTo("broken spine");
    }

    @Test
    @DisplayName("markLost sets status to LOST")
    void markLost() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.markLost();
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.LOST);
    }

    @Test
    @DisplayName("management update changes copy state and trims blank notes")
    void updateManagement() {
        BookCopy copy = BookCopy.create(10L, BookCopyCondition.NEW);
        copy.updateManagement(BookCopyStatus.MAINTENANCE, BookCopyCondition.FAIR, "  ");
        assertThat(copy.getStatus()).isEqualTo(BookCopyStatus.MAINTENANCE);
        assertThat(copy.getCondition()).isEqualTo(BookCopyCondition.FAIR);
        assertThat(copy.getNotes()).isNull();
    }
}
