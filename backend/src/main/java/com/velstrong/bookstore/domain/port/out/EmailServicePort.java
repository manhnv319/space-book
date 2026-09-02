package com.velstrong.bookstore.domain.port.out;

public interface EmailServicePort {
    void sendPasswordResetEmail(String toEmail, String otp);
    void sendEmailVerificationEmail(String toEmail, String otp, String username);
    void sendWelcomeEmail(String toEmail, String username);
    void sendOrderConfirmationEmail(String toEmail, String orderCode);
}
