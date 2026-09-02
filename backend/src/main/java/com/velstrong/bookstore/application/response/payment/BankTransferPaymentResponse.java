package com.velstrong.bookstore.application.response.payment;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * `expiresAt` là {@link Instant} chứ không phải {@code LocalDateTime}.
 *
 * Máy chủ chạy UTC còn khách ở +07. Một mốc thời gian không mang múi giờ
 * ("2026-07-26T19:00") bị trình duyệt hiểu là giờ địa phương, nên hạn chuyển
 * khoản lùi đi đúng bằng chênh lệch múi giờ và đơn vừa tạo đã báo quá hạn.
 * Instant serialize kèm hậu tố Z nên không còn chỗ để hiểu nhầm.
 */
public record BankTransferPaymentResponse(
        Long orderId,
        String paymentReference,
        long amount,
        String bankName,
        String accountName,
        String accountNumber,
        String qrPayload,
        Instant expiresAt,
        String status
) {
    /** Quy đổi mốc thời gian lưu trong DB (theo múi giờ máy chủ) sang một thời điểm tuyệt đối. */
    public static Instant toInstant(LocalDateTime value) {
        return value == null ? null : value.atZone(ZoneId.systemDefault()).toInstant();
    }
}
