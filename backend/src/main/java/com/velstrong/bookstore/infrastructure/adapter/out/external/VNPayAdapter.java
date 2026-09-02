package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.velstrong.bookstore.domain.port.out.VNPayPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class VNPayAdapter implements VNPayPort {

    private final String tmnCode;
    private final String hashSecret;
    private final String paymentUrl;
    private final String returnUrl;

    public VNPayAdapter(@Value("${app.vnpay.tmn-code:}") String tmnCode,
                        @Value("${app.vnpay.hash-secret:}") String hashSecret,
                        @Value("${app.vnpay.url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}") String paymentUrl,
                        @Value("${app.vnpay.return-url:}") String returnUrl) {
        this.tmnCode = tmnCode;
        this.hashSecret = hashSecret;
        this.paymentUrl = paymentUrl;
        this.returnUrl = returnUrl;
    }

    @Override
    public String createPaymentUrl(String orderCode, Long amount, String orderInfo, String ipAddress) {
        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", orderCode);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", ipAddress);
        params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));

        String queryString = buildQueryString(params);
        String secureHash = hmacSHA512(hashSecret, queryString);
        return paymentUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    @Override
    public boolean verifyIpnSignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        if (receivedHash == null) return false;

        Map<String, String> filtered = new TreeMap<>(params);
        filtered.remove("vnp_SecureHash");
        filtered.remove("vnp_SecureHashType");

        String queryString = buildQueryString(filtered);
        String computedHash = hmacSHA512(hashSecret, queryString);
        return computedHash.equalsIgnoreCase(receivedHash);
    }

    @Override
    public Map<String, String> queryTransaction(String transactionRef, String transactionDate) {
        return Map.of("vnp_TxnRef", transactionRef, "vnp_ResponseCode", "00");
    }

    @Override
    public String refund(String transactionRef, Long amount, String orderInfo) {
        return "00";
    }

    private String buildQueryString(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (sb.length() > 0) sb.append('&');
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII));
            sb.append('=');
            sb.append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }
        return sb.toString();
    }

    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA512");
            hmac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] bytes = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) result.append(String.format("%02x", b));
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA512", e);
        }
    }
}
