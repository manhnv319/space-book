package com.velstrong.bookstore.application.service.user;

import com.velstrong.bookstore.application.command.user.VerifyEmailCommand;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.user.VerifyEmailUseCase;
import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

@Service
@Transactional
public class VerifyEmailService implements VerifyEmailUseCase {
    private static final String KEY_PREFIX = "email-verification:otp:";
    private final UserRepository userRepository;
    private final PasswordResetChallengeRepository challengeRepository;
    private final byte[] hmacSecret;

    public VerifyEmailService(UserRepository userRepository,
                              PasswordResetChallengeRepository challengeRepository,
                              @Value("${app.auth.reset-hmac-secret:${app.jwt.secret}}") String hmacSecret) {
        this.userRepository = userRepository;
        this.challengeRepository = challengeRepository;
        this.hmacSecret = hmacSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void verifyEmail(VerifyEmailCommand command) {
        if (command.otp() == null || command.otp().isBlank())
            throw new InvalidOperationException("Verification code is required");
        String email = ForgotPasswordService.normalize(command.email());
        var result = challengeRepository.consume(KEY_PREFIX + hmac(email), hmac(command.otp()), 5);
        if (result.status() != PasswordResetChallengeRepository.ConsumeResult.Status.CONSUMED)
            throw new BookstoreException("Invalid or expired verification code", BookstoreException.UNAUTHORIZED);
        User user = userRepository.findById(result.userId())
                .orElseThrow(() -> new EntityNotFoundException("User", result.userId()));
        user.verifyEmail();
        userRepository.updateStatus(user.getId(), user.getStatus().name());
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash verification value", e);
        }
    }
}
