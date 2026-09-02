package com.velstrong.bookstore.application.service.rental;

import com.velstrong.bookstore.application.response.rental.RentalResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InsufficientStockException;
import com.velstrong.bookstore.domain.model.BookCopy;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.Rental;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.domain.port.in.rental.StartRentalUseCase;
import com.velstrong.bookstore.domain.port.out.BookCopyRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class StartRentalService implements StartRentalUseCase {

    private static final RentalTermUnit DEFAULT_RENTAL_TERM_UNIT = RentalTermUnit.MONTH;
    private static final int DEFAULT_RENTAL_TERM_VALUE = 1;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final BookCopyRepository bookCopyRepository;
    private final RentalRepository rentalRepository;

    public StartRentalService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                              BookCopyRepository bookCopyRepository, RentalRepository rentalRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.bookCopyRepository = bookCopyRepository;
        this.rentalRepository = rentalRepository;
    }

    @Override
    public List<RentalResponse> startFromOrder(Long orderId) {
        Order order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        List<Rental> rentals = new ArrayList<>();

        for (OrderItem item : orderItemRepository.findByOrderId(orderId)) {
            if (!item.isRental() || rentalRepository.existsByOrderItemId(item.getId())) continue;

            BookCopy bookCopy = bookCopyRepository.findFirstAvailableByBookIdForUpdate(item.getBookId())
                    .orElseThrow(() -> new InsufficientStockException(
                            "No available book copy for book " + item.getBookId()));
            bookCopy.markRented();
            bookCopyRepository.save(bookCopy);

            RentalTermUnit termUnit = item.getRentalTermUnit() != null
                    ? item.getRentalTermUnit() : DEFAULT_RENTAL_TERM_UNIT;
            int termValue = item.getRentalTermValue() != null && item.getRentalTermValue() > 0
                    ? item.getRentalTermValue() : DEFAULT_RENTAL_TERM_VALUE;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = switch (termUnit) {
                case DAY -> startDate.plusDays(termValue);
                case WEEK -> startDate.plusWeeks(termValue);
                case MONTH -> startDate.plusMonths(termValue);
            };

            rentals.add(rentalRepository.save(Rental.create(
                    item.getId(), bookCopy.getId(), order.getUserId(), termUnit, termValue,
                    item.getDepositAmount() != null ? item.getDepositAmount() : 0L, startDate, endDate)));
        }

        return rentals.stream().map(RentalResponse::from).toList();
    }
}
