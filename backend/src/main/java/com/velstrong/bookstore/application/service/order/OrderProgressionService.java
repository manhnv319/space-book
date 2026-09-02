package com.velstrong.bookstore.application.service.order;

import com.velstrong.bookstore.domain.model.Order;
import com.velstrong.bookstore.domain.model.OrderStatusChange;
import com.velstrong.bookstore.domain.model.enums.order.OrderStatus;
import com.velstrong.bookstore.domain.port.out.OrderRepository;
import com.velstrong.bookstore.domain.port.out.OrderStatusHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Đẩy đơn đã thanh toán đi hết lộ trình giao hàng.
 *
 * Chưa tích hợp đơn vị vận chuyển nào, nên mỗi chặng được mô phỏng bằng một
 * khoảng thời gian cố định. Toàn bộ phần còn lại — lịch sử trạng thái, API, giao
 * diện — hoạt động y như khi có dữ liệu vận chuyển thật; chỉ nguồn sự kiện là
 * giả. Khi nối được hãng vận chuyển, thay chỗ này và không phải sửa gì thêm.
 */
@Service
public class OrderProgressionService {

    private static final Logger log = LoggerFactory.getLogger(OrderProgressionService.class);

    /** Chặng kế tiếp của mỗi trạng thái. Trạng thái không có ở đây là điểm dừng. */
    private static final Map<OrderStatus, OrderStatus> NEXT = Map.of(
            OrderStatus.CONFIRMED, OrderStatus.PROCESSING,
            OrderStatus.PROCESSING, OrderStatus.SHIPPING,
            OrderStatus.SHIPPING, OrderStatus.COMPLETED);

    private final OrderRepository orders;
    private final OrderStatusHistoryRepository history;

    public OrderProgressionService(OrderRepository orders, OrderStatusHistoryRepository history) {
        this.orders = orders;
        this.history = history;
    }

    @Transactional
    public int advanceOrdersOlderThan(LocalDateTime cutoff) {
        List<Order> due = orders.findAdvanceable(List.copyOf(NEXT.keySet()), cutoff);
        int advanced = 0;
        for (Order order : due) {
            OrderStatus next = NEXT.get(order.getStatus());
            if (next == null) continue;
            order.updateStatus(next);
            orders.save(order);
            history.record(order.getId(), next, OrderStatusChange.SOURCE_AUTO, LocalDateTime.now());
            advanced++;
        }
        if (advanced > 0) log.info("Advanced {} order(s) along the delivery route", advanced);
        return advanced;
    }
}
