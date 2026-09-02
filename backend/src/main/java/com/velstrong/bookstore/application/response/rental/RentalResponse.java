package com.velstrong.bookstore.application.response.rental;

import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalStatus;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;

import java.time.LocalDate;

/**
 * `bookId`/`bookTitle` được nạp thêm từ bản sao sách.
 *
 * Bản thân phiếu thuê chỉ giữ `bookCopyId`, nên nếu không nạp kèm thì màn "sách
 * đang thuê" chỉ hiển thị được số hiệu bản sao — vô nghĩa với người đọc. Cả hai
 * có thể null khi bản sao hoặc đầu sách đã bị xoá.
 */
public record RentalResponse(
        Long id,
        Long bookCopyId,
        Long bookId,
        String bookTitle,
        Long userId,
        RentalTermUnit rentalTermUnit,
        Integer rentalTermValue,
        Long depositAmount,
        LocalDate rentalStartDate,
        LocalDate plannedReturnDate,
        LocalDate actualReturnDate,
        RentalStatus status,
        Integer lateDays,
        Long lateFeeAmount,
        Long damageFeeAmount
) {
    public static RentalResponse from(Rental rental) {
        return from(rental, null, null);
    }

    public static RentalResponse from(Rental rental, Long bookId, String bookTitle) {
        return new RentalResponse(
                rental.getId(), rental.getBookCopyId(), bookId, bookTitle, rental.getUserId(),
                rental.getRentalTermUnit(), rental.getRentalTermValue(),
                rental.getDepositAmount(), rental.getRentalStartDate(),
                rental.getPlannedReturnDate(), rental.getActualReturnDate(),
                rental.getStatus(), rental.getLateDays(),
                rental.getLateFeeAmount(), rental.getDamageFeeAmount()
        );
    }
}
