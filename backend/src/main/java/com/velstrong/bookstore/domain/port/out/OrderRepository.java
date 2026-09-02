package com.velstrong.bookstore.domain.port.out;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.PageResult;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.model.enums.order.PaymentStatus;

import java.time.LocalDate;
import java.util.Optional;

public interface OrderRepository {
    /**
     * Đơn ĐÃ THANH TOÁN đang ở một trong các trạng thái đã cho và không đổi gì
     * kể từ trước `cutoff` — tức đã ở chặng hiện tại đủ lâu để đi tiếp.
     */
    java.util.List<Order> findAdvanceable(java.util.List<com.velstrong.bookstore.domain.model.enums.order.OrderStatus> statuses,
                                          java.time.LocalDateTime cutoff);

    Order save(Order order);
    Optional<Order> findById(Long id);
    Optional<Order> findByIdForUpdate(Long id);
    Optional<Order> findByOrderCode(String orderCode);
    /**
     * Lọc theo nhiều trạng thái cùng lúc.
     *
     * Một tab của khách gom nhiều trạng thái nội bộ ("Đang chuẩn bị" = CONFIRMED
     * + PROCESSING), nên lọc một trạng thái mỗi lần thì không phân trang đúng
     * được — phải lấy rộng rồi cắt ở FE, và số trang sẽ sai.
     */
    PageResult<Order> findByUserIdAndStatuses(Long userId, java.util.List<OrderStatus> statuses,
                                              int page, int size);

    /** Số đơn theo từng trạng thái, cho số đếm trên tab. */
    java.util.Map<OrderStatus, Long> countByStatusForUser(Long userId);

    PageResult<Order> findByUserId(Long userId, OrderStatus status, PaymentStatus paymentStatus,
                                    int page, int size);
    PageResult<Order> findAll(OrderStatus status, PaymentStatus paymentStatus, int page, int size,
                               LocalDate fromDate, LocalDate toDate, String search);
    boolean existsByOrderCode(String orderCode);
}
