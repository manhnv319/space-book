package com.velstrong.bookstore.infrastructure.adapter.out.external;

import com.velstrong.bookstore.domain.port.out.EmailServicePort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailAdapter implements EmailServicePort {
    private final JavaMailSender mailSender;
    private final String from;

    public EmailAdapter(JavaMailSender mailSender,
                        @Value("${spring.mail.username:noreply@bookstore.com}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String otp) {
        sendHtml(toEmail, "Đặt lại mật khẩu · Velstrong", "Đặt lại mật khẩu",
                "Mã xác minh của bạn", otp,
                "Mã có hiệu lực trong 15 phút. Nếu bạn không yêu cầu thao tác này, bạn có thể bỏ qua email.");
    }

    @Override
    public void sendEmailVerificationEmail(String toEmail, String otp, String username) {
        sendHtml(toEmail, "Xác minh email · Velstrong", "Chào mừng bạn đến với Velstrong",
                "Mã xác minh email của bạn", otp,
                "Mã có hiệu lực trong 15 phút. Hoàn tất xác minh để bắt đầu sử dụng tài khoản.");
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String username) {
        sendHtml(toEmail, "Chào mừng đến Velstrong", "Chào mừng, " + escape(username) + "!",
                "Tài khoản của bạn đã sẵn sàng.", null,
                "Cảm ơn bạn đã tham gia VelstrongBook.");
    }

    @Override
    public void sendOrderConfirmationEmail(String toEmail, String orderCode) {
        sendHtml(toEmail, "Xác nhận đơn hàng · Velstrong", "Đơn hàng đã được xác nhận",
                "Mã đơn hàng", escape(orderCode), "Cảm ơn bạn đã mua sắm tại VelstrongBook.");
    }

    private void sendHtml(String to, String subject, String title, String label, String code, String note) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(template(title, label, code, note), true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Unable to build email", e);
        }
    }

    private String template(String title, String label, String code, String note) {
        String codeBlock = code == null ? "" : "<div style=\"margin:22px 0;padding:18px;border:1px solid #d7b15a;background:#fffaf0;text-align:center;color:#1b3559;font-size:30px;font-weight:700;letter-spacing:.25em\">" + code + "</div>";
        return """
                <!doctype html><html><body style="margin:0;background:#f4f1eb;color:#172b4d;font-family:Arial,sans-serif">
                <div style="max-width:560px;margin:32px auto;padding:0 16px">
                  <div style="background:#1b3559;color:#fff;padding:22px 28px;border-top:4px solid #d7b15a;font-size:20px;font-weight:700;letter-spacing:-.02em">Velstrong</div>
                  <div style="background:#fff;padding:36px 28px;border:1px solid #e1ddd5">
                    <p style="margin:0 0 12px;color:#8e2f25;font-size:12px;text-transform:uppercase;letter-spacing:.12em">Velstrong / Tài khoản</p>
                    <h1 style="margin:0 0 24px;font-size:28px;line-height:1.15">%s</h1>
                    <p style="margin:0 0 10px;color:#555;font-size:15px">%s</p>
                    %s
                    <p style="margin:24px 0 0;color:#6d7889;font-size:13px;line-height:1.6">%s</p>
                  </div>
                  <p style="margin:16px 0;text-align:center;color:#8b96a6;font-size:12px">Email tự động từ Velstrong · Vui lòng không trả lời</p>
                </div></body></html>
                """.formatted(escape(title), escape(label), codeBlock, escape(note));
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
