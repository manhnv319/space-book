package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.port.out.QrImagePort;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Renders the VietQR payload server-side so the storefront needs no QR encoder
 * and no third-party image service ever sees the account number or amount.
 */
@Component
public class ZxingQrImageGenerator implements QrImagePort {

    private static final int MIN_SIZE = 128;
    private static final int MAX_SIZE = 1024;

    @Override
    public byte[] renderPng(String payload, int size) {
        if (payload == null || payload.isBlank()) throw new InvalidOperationException("QR payload is empty");
        int side = Math.clamp(size, MIN_SIZE, MAX_SIZE);

        Map<EncodeHintType, Object> hints = Map.of(
                EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name(),
                // Banking apps scan from phone screens at an angle; M leaves room
                // for that without inflating the matrix the way H would.
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN, 2);

        try (ByteArrayOutputStream png = new ByteArrayOutputStream()) {
            BitMatrix matrix = new QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, side, side, hints);
            MatrixToImageWriter.writeToStream(matrix, "PNG", png);
            return png.toByteArray();
        } catch (WriterException | IOException e) {
            throw new InvalidOperationException("Could not render the transfer QR code");
        }
    }
}
