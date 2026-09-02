package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZxingQrImageGeneratorTest {

    private final ZxingQrImageGenerator generator = new ZxingQrImageGenerator();
    private final VietQrGenerator vietQr = new VietQrGenerator();

    /** Decodes the PNG the same way a banking app's scanner would. */
    private String decode(byte[] png) throws Exception {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(png));
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        return new QRCodeReader().decode(bitmap).getText();
    }

    @Test
    void producesAPngThatScansBackToTheVietQrPayload() throws Exception {
        // A payload shaped like the real thing: BVBank BIN, account, amount, reference.
        String payload = vietQr.createQrPayload("970454", "9021003210798", 248_000L, "TQST7K2M9");

        byte[] png = generator.renderPng(payload, 320);

        assertThat(decode(png)).isEqualTo(payload);
    }

    @Test
    void rendersTheRequestedSizeAndClampsAbsurdOnes() throws Exception {
        assertThat(ImageIO.read(new ByteArrayInputStream(generator.renderPng("hello", 320))).getWidth())
                .isEqualTo(320);
        // A caller passing ?size=99999 must not turn into a multi-megabyte render.
        assertThat(ImageIO.read(new ByteArrayInputStream(generator.renderPng("hello", 99_999))).getWidth())
                .isEqualTo(1024);
        assertThat(ImageIO.read(new ByteArrayInputStream(generator.renderPng("hello", 1))).getWidth())
                .isEqualTo(128);
    }

    @Test
    void refusesAnEmptyPayloadRatherThanRenderingAnUnscannableSquare() {
        assertThatThrownBy(() -> generator.renderPng("", 320)).isInstanceOf(InvalidOperationException.class);
        assertThatThrownBy(() -> generator.renderPng(null, 320)).isInstanceOf(InvalidOperationException.class);
    }
}
