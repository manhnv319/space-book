package com.velstrong.bookstore.infrastructure.adapter.out.external;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailAdapterTest {

    private final JavaMailSender mailSender = mock(JavaMailSender.class);
    private final List<MimeMessage> sentMessages = new ArrayList<>();
    private final EmailAdapter adapter = new EmailAdapter(mailSender, "noreply@example.test");

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenAnswer(invocation -> new MimeMessage(Session.getInstance(new Properties())));
        doAnswer(invocation -> {
            sentMessages.add(invocation.getArgument(0));
            return null;
        }).when(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void identifiesAllAccountEmailsAsSachNha() throws Exception {
        adapter.sendPasswordResetEmail("reader@example.test", "123456");
        adapter.sendEmailVerificationEmail("reader@example.test", "654321", "reader");
        adapter.sendWelcomeEmail("reader@example.test", "reader");
        adapter.sendOrderConfirmationEmail("reader@example.test", "DH-001");

        assertThat(sentMessages).extracting(MimeMessage::getSubject).containsExactly(
                "Đặt lại mật khẩu · Sách Nhà",
                "Xác minh email · Sách Nhà",
                "Chào mừng đến Sách Nhà",
                "Xác nhận đơn hàng · Sách Nhà");
        assertThat(sentMessages).allSatisfy(message -> {
            try {
                assertThat((String) message.getContent()).contains("Sách Nhà").doesNotContain("Velstrong");
            } catch (Exception exception) {
                throw new AssertionError("Expected a readable HTML email", exception);
            }
        });
    }
}
