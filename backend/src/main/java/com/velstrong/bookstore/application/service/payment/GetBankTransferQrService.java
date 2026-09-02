package com.velstrong.bookstore.application.service.payment;

import com.velstrong.bookstore.application.response.payment.BankTransferPaymentResponse;
import com.velstrong.bookstore.domain.port.in.payment.CreateBankTransferPaymentUseCase;
import com.velstrong.bookstore.domain.port.in.payment.GetBankTransferQrUseCase;
import com.velstrong.bookstore.domain.port.out.QrImagePort;
import org.springframework.stereotype.Service;

/**
 * Renders the transfer QR for an order.
 *
 * Delegates to {@link CreateBankTransferPaymentUseCase} rather than reading the
 * payment itself, so ownership, expiry and the "is bank transfer configured"
 * rules are enforced in exactly one place — and the reference is minted there
 * too, meaning the QR can never encode a payload the reconciler would not
 * recognise.
 */
@Service
public class GetBankTransferQrService implements GetBankTransferQrUseCase {

    private final CreateBankTransferPaymentUseCase bankTransfers;
    private final QrImagePort qrImages;

    public GetBankTransferQrService(CreateBankTransferPaymentUseCase bankTransfers, QrImagePort qrImages) {
        this.bankTransfers = bankTransfers;
        this.qrImages = qrImages;
    }

    @Override
    public byte[] renderQr(Long orderId, Long userId, int size) {
        BankTransferPaymentResponse payment = bankTransfers.create(orderId, userId);
        return qrImages.renderPng(payment.qrPayload(), size);
    }
}
