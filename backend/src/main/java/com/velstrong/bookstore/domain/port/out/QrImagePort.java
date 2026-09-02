package com.velstrong.bookstore.domain.port.out;

public interface QrImagePort {
    /** Renders a QR payload as a square PNG of {@code size} pixels. */
    byte[] renderPng(String payload, int size);
}
