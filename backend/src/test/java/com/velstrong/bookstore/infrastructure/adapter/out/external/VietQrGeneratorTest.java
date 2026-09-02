package com.velstrong.bookstore.infrastructure.adapter.out.external;

import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A QR that renders is not a QR that scans. Banking apps rejected the previous
 * payload as invalid because field 38 was assembled in the wrong order: the
 * bank and account sat under sub-tag 02, which is where the service code
 * belongs, so nothing could resolve the beneficiary.
 *
 * These assert the decoded structure rather than a golden string, so a change
 * that keeps the code parseable but moves a field still fails.
 */
class VietQrGeneratorTest {

    private final VietQrGenerator generator = new VietQrGenerator();

    /** Splits an EMVCo tag-length-value string into its tags. */
    private static Map<String, String> tlv(String value) {
        Map<String, String> fields = new LinkedHashMap<>();
        int index = 0;
        while (index + 4 <= value.length()) {
            String tag = value.substring(index, index + 2);
            int length = Integer.parseInt(value.substring(index + 2, index + 4));
            fields.put(tag, value.substring(index + 4, index + 4 + length));
            index += 4 + length;
        }
        return fields;
    }

    private String payload() {
        return generator.createQrPayload("970454", "9021003210798", 85_000L, "TQ4Z3BAH");
    }

    @Test
    void putsTheBankAndAccountUnderTheBeneficiaryTag() {
        Map<String, String> merchantAccount = tlv(tlv(payload()).get("38"));

        assertThat(merchantAccount.get("00")).as("NAPAS GUID").isEqualTo("A000000727");
        assertThat(merchantAccount.get("02")).as("transfer-to-account service code").isEqualTo("QRIBFTTA");

        Map<String, String> beneficiary = tlv(merchantAccount.get("01"));
        assertThat(beneficiary.get("00")).as("bank BIN").isEqualTo("970454");
        assertThat(beneficiary.get("01")).as("account number").isEqualTo("9021003210798");
    }

    @Test
    void carriesTheAmountCurrencyCountryAndMemo() {
        Map<String, String> fields = tlv(payload());

        assertThat(fields.get("00")).isEqualTo("01");
        assertThat(fields.get("01")).as("dynamic QR, because it carries an amount").isEqualTo("12");
        assertThat(fields.get("53")).as("VND").isEqualTo("704");
        assertThat(fields.get("54")).isEqualTo("85000");
        assertThat(fields.get("58")).isEqualTo("VN");
        // The memo is what the reconciler matches an incoming credit against.
        assertThat(tlv(fields.get("62")).get("08")).isEqualTo("TQ4Z3BAH");
    }

    @Test
    void endsWithAChecksumOverEverythingBeforeIt() {
        String payload = payload();

        assertThat(payload).contains("6304");
        String body = payload.substring(0, payload.length() - 4);
        assertThat(body).endsWith("6304");
        assertThat(payload.substring(payload.length() - 4)).matches("[0-9A-F]{4}");

        // Recomputing over the body must reproduce the trailing digits, which is
        // the first thing any scanner checks.
        assertThat(crc16(body)).isEqualTo(payload.substring(payload.length() - 4));
    }

    @Test
    void matchesTheStructureOfNapasPublishedExample() {
        // Documented sample: bank 970418, account 1234567890, 10.000 VND, memo "test".
        String expectedBody = "000201010212" + "38540010A00000072701240006970418011012345678900208QRIBFTTA"
                + "5303704" + "540510000" + "5802VN" + "62080804test" + "6304";

        String actual = generator.createQrPayload("970418", "1234567890", 10_000L, "test");

        assertThat(actual).isEqualTo(expectedBody + crc16(expectedBody));
    }

    /** CRC-16/CCITT-FALSE, mirrored from the generator so the test is independent of it. */
    private static String crc16(String value) {
        int crc = 0xFFFF;
        for (byte item : value.getBytes(java.nio.charset.StandardCharsets.US_ASCII)) {
            crc ^= (item & 0xFF) << 8;
            for (int bit = 0; bit < 8; bit++) crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
            crc &= 0xFFFF;
        }
        return String.format("%04X", crc);
    }
}
