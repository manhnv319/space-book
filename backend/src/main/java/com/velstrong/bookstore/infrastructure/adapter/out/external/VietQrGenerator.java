package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.velstrong.bookstore.domain.port.out.BankTransferPort;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class VietQrGenerator implements BankTransferPort {
    /** NAPAS application id for VietQR. */
    private static final String GUID = "A000000727";

    /** Interbank funds transfer to an account number (as opposed to a card, QRIBFTTC). */
    private static final String SERVICE_TRANSFER_TO_ACCOUNT = "QRIBFTTA";

    @Override
    public String createQrPayload(String bankBin, String accountNumber, long amount, String reference) {
        // Field 38 has a fixed shape: 00 is the GUID, 01 is a nested TLV holding
        // the acquirer (bank BIN) and the account, and 02 is the service code.
        // Putting the bank and account anywhere else leaves a banking app unable
        // to resolve the beneficiary, and it rejects the code as invalid.
        String beneficiary = field("00", bankBin) + field("01", accountNumber);
        String merchantAccount = field("00", GUID) + field("01", beneficiary)
                + field("02", SERVICE_TRANSFER_TO_ACCOUNT);

        String payload = field("00", "01")
                + field("01", "12")                       // dynamic QR: carries an amount, single use
                + field("38", merchantAccount)
                + field("53", "704")                      // VND
                + field("54", Long.toString(amount))
                + field("58", "VN")
                + field("62", field("08", reference))     // 08 = purpose of transaction (the memo)
                + "6304";                                 // CRC tag and length, checksum covers them
        return payload + crc16(payload);
    }

    private String field(String id, String value) {
        int length = value.getBytes(StandardCharsets.UTF_8).length;
        if (length > 99) throw new IllegalArgumentException("VietQR field is too long");
        return id + String.format("%02d", length) + value;
    }

    private String crc16(String value) {
        int crc = 0xFFFF;
        for (byte item : value.getBytes(StandardCharsets.US_ASCII)) {
            crc ^= (item & 0xFF) << 8;
            for (int bit = 0; bit < 8; bit++) crc = (crc & 0x8000) != 0 ? (crc << 1) ^ 0x1021 : crc << 1;
            crc &= 0xFFFF;
        }
        return String.format("%04X", crc);
    }
}
