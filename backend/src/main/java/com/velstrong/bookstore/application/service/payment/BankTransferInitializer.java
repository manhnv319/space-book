package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.Payment;
import com.velstrong.bookstore.domain.port.out.BankTransferPort;
import com.velstrong.bookstore.domain.port.out.BankTransferSettingsPort;
import com.velstrong.bookstore.domain.port.out.PaymentRepository;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Gán mã chuyển khoản cho một khoản tiền và dựng thông tin để hiển thị QR.
 *
 * Dùng chung cho cả đơn hàng lẫn gói thuê tháng: mã chuyển khoản là thứ duy nhất
 * bộ đối soát dùng để khớp báo có, nên nó phải được sinh ở đúng một chỗ. Hai chỗ
 * sinh mã theo hai cách là con đường nhanh nhất tới một khoản tiền không khớp
 * được với thứ gì.
 */
@Component
public class BankTransferInitializer {

    /** Không có 0/O/1/I/L — khách gõ tay khi ứng dụng ngân hàng không quét được QR. */
    private static final String ALPHABET = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    private static final int RANDOM_SUFFIX_LENGTH = 5;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final PaymentRepository payments;
    private final BankTransferPort qr;
    private final BankTransferSettingsPort settings;

    public BankTransferInitializer(PaymentRepository payments, BankTransferPort qr,
                                   BankTransferSettingsPort settings) {
        this.payments = payments;
        this.qr = qr;
        this.settings = settings;
    }

    public void requireConfigured() {
        if (!settings.isConfigured()) throw new InvalidOperationException("Bank transfer is not configured");
    }

    /**
     * Trả về thông tin chuyển khoản, sinh mã ở lần gọi đầu.
     *
     * Gọi lại nhiều lần vẫn ra đúng mã cũ, nên khách reload trang thanh toán
     * không làm hỏng mã đã in trên QR đang quét dở.
     */
    public BankTransferPaymentResponse initialize(Payment payment, String referencePrefix, Long targetId) {
        if (payment.isSuccess()) return describe(payment);
        if (payment.isExpired(LocalDateTime.now()))
            throw new InvalidOperationException("Bank transfer payment has expired");

        if (payment.getTransferReference() == null) {
            payment.initializeBankTransfer(newReference(referencePrefix, targetId),
                    LocalDateTime.now().plusMinutes(settings.expiryMinutes()));
            payment = payments.save(payment);
        }
        return describe(payment);
    }

    public BankTransferPaymentResponse describe(Payment payment) {
        String payload = qr.createQrPayload(settings.bankBin(), settings.accountNumber(), payment.getAmount(),
                payment.getTransferReference());
        return new BankTransferPaymentResponse(payment.getOrderId(), payment.getTransferReference(),
                payment.getAmount(), settings.bankName(), settings.accountName(), settings.accountNumber(),
                payload, BankTransferPaymentResponse.toInstant(payment.getExpiresAt()),
                payment.getStatus().name());
    }

    /**
     * Mã khách ghi vào nội dung chuyển khoản.
     *
     * Có phần ngẫu nhiên vì chỉ dựa vào id thì đoán được (đơn số 1 thành "TQ1"),
     * mà đây chính là thứ dùng để khớp tiền vào với đúng người.
     */
    private String newReference(String prefix, Long targetId) {
        StringBuilder reference = new StringBuilder(prefix).append(Long.toString(targetId, 36).toUpperCase());
        for (int i = 0; i < RANDOM_SUFFIX_LENGTH; i++) {
            reference.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return reference.toString();
    }
}
