package com.velstrong.bookstore.application.service.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.order.CreateOrderCommand;
import com.velstrong.bookstore.application.command.voucher.QuoteVoucherCommand;
import com.velstrong.bookstore.application.command.voucher.ReserveVoucherCommand;
import com.velstrong.bookstore.application.response.order.OrderResponse;
import com.velstrong.bookstore.application.response.voucher.VoucherQuoteResponse;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Book;
import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderItem;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.model.enums.order.ItemType;
import com.velstrong.bookstore.domain.model.enums.order.OrderType;
import com.velstrong.bookstore.domain.model.enums.rental.RentalTermUnit;
import com.velstrong.bookstore.domain.port.in.order.CreateOrderUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.QuoteVoucherUseCase;
import com.velstrong.bookstore.domain.port.in.voucher.ReserveVoucherUseCase;
import com.velstrong.bookstore.domain.model.CartItem;
import com.velstrong.bookstore.domain.port.out.BookRepository;
import com.velstrong.bookstore.domain.port.out.CartItemRepository;
import com.velstrong.bookstore.domain.port.out.CartRepository;
import com.velstrong.bookstore.domain.port.out.OrderItemRepository;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.service.RentalPricing;
import com.github.f4b6a3.ulid.UlidCreator;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class CreateOrderService implements CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final QuoteVoucherUseCase quoteVoucherUseCase;
    private final ReserveVoucherUseCase reserveVoucherUseCase;

    public CreateOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                               PaymentRepository paymentRepository, UserRepository userRepository,
                               BookRepository bookRepository, CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               QuoteVoucherUseCase quoteVoucherUseCase, ReserveVoucherUseCase reserveVoucherUseCase) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.quoteVoucherUseCase = quoteVoucherUseCase;
        this.reserveVoucherUseCase = reserveVoucherUseCase;
    }

    @Override
    public OrderResponse create(CreateOrderCommand command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", command.userId()));

        if (command.items() == null || command.items().isEmpty())
            throw new InvalidOperationException("Order must have at least one item");
        if (command.paymentMethod() == null)
            throw new InvalidOperationException("Payment method is required");
        if (command.shippingAddressId() == null)
            throw new InvalidOperationException("Shipping address is required");

        List<OrderItem> orderItems = buildOrderItems(command.items());
        OrderType orderType = determineOrderType(command.items());
        String orderCode = generateOrderCode();

        Order order = Order.create(command.userId(), orderCode, orderType,
                command.paymentMethod(), command.shippingAddressId(), command.notes());
        order.setItems(orderItems);
        order.calculateTotals();

        if (command.voucherCode() != null && !command.voucherCode().isBlank()) {
            VoucherQuoteResponse quote = quoteVoucherUseCase.quote(
                    new QuoteVoucherCommand(command.userId(), command.voucherCode(), order.getTotalAmount()));
            if (quote.valid()) {
                order.setTotalDiscount(quote.discountAmount());
                order.calculateTotals();
            }
        }

        Order saved = orderRepository.save(order);
        orderItems.forEach(item -> item.setOrderId(saved.getId()));
        List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);
        saved.setItems(savedItems);

        if (command.voucherCode() != null && !command.voucherCode().isBlank()
                && order.getTotalDiscount() != null && order.getTotalDiscount() > 0) {
            reserveVoucherUseCase.reserve(new ReserveVoucherCommand(
                    command.userId(), command.voucherCode(), saved.getId(), saved.getTotalAmount()));
        }

        Payment payment = Payment.create(saved.getId(), saved.getFinalAmount(), command.paymentMethod());
        paymentRepository.save(payment);

        removeOrderedItemsFromCart(command);

        return OrderResponse.from(saved);
    }

    // Prices are computed authoritatively from the Book (Validation S1); client-sent prices are ignored.
    private List<OrderItem> buildOrderItems(List<CreateOrderCommand.Item> items) {
        List<OrderItem> result = new ArrayList<>();
        for (CreateOrderCommand.Item item : items) {
            Book book = bookRepository.findById(item.bookId())
                    .orElseThrow(() -> new EntityNotFoundException("Book", item.bookId()));
            if (item.itemType() == ItemType.PURCHASE) {
                if (!book.isAvailableForSale())
                    throw new InvalidOperationException("Book is not available for sale: " + book.getId());
                int quantity = item.quantity() != null && item.quantity() > 0 ? item.quantity() : 1;
                result.add(OrderItem.createPurchase(item.bookId(), quantity, book.getListPrice()));
            } else {
                if (!book.isAvailableForRental())
                    throw new InvalidOperationException("Book is not available for rental: " + book.getId());
                int termValue = item.rentalTermValue() != null && item.rentalTermValue() > 0
                        ? item.rentalTermValue() : 1;
                RentalTermUnit termUnit = rentalTermUnit(item.rentalTermUnit());
                long rentalFee = RentalPricing.rentalFee(book, termValue, termUnit.name());
                result.add(OrderItem.createRental(item.bookId(), item.bookCopyId(), rentalFee,
                        RentalPricing.deposit(book), termValue, termUnit));
            }
        }
        return result;
    }

    private RentalTermUnit rentalTermUnit(String rawTermUnit) {
        if (rawTermUnit == null || rawTermUnit.isBlank()) return RentalTermUnit.WEEK;
        try {
            return RentalTermUnit.valueOf(rawTermUnit.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            return RentalTermUnit.WEEK;
        }
    }

    /**
     * Bỏ khỏi giỏ những gì vừa được đặt.
     *
     * Không làm việc này thì sách đã đặt vẫn nằm trong giỏ, và khách bấm "Đặt
     * đơn" lần nữa sẽ tạo một đơn trùng cho đúng những cuốn đó — rồi có thể trả
     * tiền hai lần.
     *
     * Chỉ gỡ đúng những dòng đã đặt, không dọn sạch giỏ: API cho phép đặt một
     * phần, và xoá cả giỏ sẽ nuốt mất những cuốn khách vẫn còn muốn mua. Đặt ít
     * hơn số đang có thì trừ bớt số lượng thay vì xoá cả dòng.
     *
     * Nằm trong cùng transaction với việc tạo đơn, nên không có trạng thái lửng
     * lơ kiểu đơn đã tạo mà giỏ chưa dọn.
     */
    private void removeOrderedItemsFromCart(CreateOrderCommand command) {
        cartRepository.findByUserId(command.userId()).ifPresent(cart -> {
            for (CreateOrderCommand.Item ordered : command.items()) {
                cartItemRepository.findMatching(cart.getId(), ordered.bookId(), ordered.itemType(),
                                ordered.rentalTermValue(), ordered.rentalTermUnit())
                        .ifPresent(line -> reduceOrRemove(line, ordered.quantity()));
            }
        });
    }

    private void reduceOrRemove(CartItem line, Integer orderedQuantity) {
        int ordered = orderedQuantity == null ? 1 : orderedQuantity;
        int inCart = line.getQuantity() == null ? 1 : line.getQuantity();
        if (ordered < inCart) {
            line.updateQuantity(inCart - ordered);
            cartItemRepository.save(line);
        } else {
            cartItemRepository.deleteById(line.getId());
        }
    }

    private OrderType determineOrderType(List<CreateOrderCommand.Item> items) {
        boolean hasPurchase = items.stream().anyMatch(i -> ItemType.PURCHASE == i.itemType());
        boolean hasRental = items.stream().anyMatch(i -> ItemType.RENTAL == i.itemType());
        if (hasPurchase && hasRental) return OrderType.MIXED;
        if (hasRental) return OrderType.RENTAL;
        return OrderType.PURCHASE;
    }

    private String generateOrderCode() {
        String code;
        int attempts = 0;
        do {
            code = "ORD-" + UlidCreator.getUlid().toString();
            attempts++;
            if (attempts > 5) throw new InvalidOperationException("Cannot generate unique order code");
        } while (orderRepository.existsByOrderCode(code));
        return code;
    }
}
