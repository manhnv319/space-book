package com.velstrong.bookstore.infrastructure.adapter.in.scheduler;

import com.velstrong.bookstore.application.service.order.OrderProgressionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Inbound adapter: nhịp đồng hồ cho lộ trình giao hàng mô phỏng.
 *
 * Chạy dày hơn khoảng cách giữa hai chặng để đơn không phải chờ quá lâu so với
 * mốc đến hạn, nhưng mỗi lần chạy chỉ đẩy tối đa một chặng cho mỗi đơn.
 */
@Component
public class OrderProgressionScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrderProgressionScheduler.class);

    private final OrderProgressionService progression;
    private final int stageMinutes;

    public OrderProgressionScheduler(OrderProgressionService progression,
                                     @Value("${app.order.progression.stage-minutes:2}") int stageMinutes) {
        this.progression = progression;
        this.stageMinutes = stageMinutes;
    }

    @Scheduled(fixedDelayString = "${app.order.progression.poll-delay-ms:30000}")
    public void advance() {
        try {
            progression.advanceOrdersOlderThan(LocalDateTime.now().minusMinutes(stageMinutes));
        } catch (Exception e) {
            log.warn("Order progression pass failed: {}", e.getClass().getSimpleName());
        }
    }
}
