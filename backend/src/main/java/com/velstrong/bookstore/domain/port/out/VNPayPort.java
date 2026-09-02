package com.velstrong.bookstore.domain.port.out;

import java.util.Map;

public interface VNPayPort {
    String createPaymentUrl(String orderCode, Long amount, String orderInfo, String ipAddress);
    boolean verifyIpnSignature(Map<String, String> params);
    Map<String, String> queryTransaction(String transactionRef, String transactionDate);
    String refund(String transactionRef, Long amount, String orderInfo);
}
