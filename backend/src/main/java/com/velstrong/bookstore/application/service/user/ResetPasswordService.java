package com.velstrong.bookstore.application.service.user;

import com.velstrong.bookstore.application.command.user.ResetPasswordCommand;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.exception.InvalidOperationException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.user.ResetPasswordUseCase;
import com.velstrong.bookstore.domain.port.out.PasswordEncoder;
import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
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
public class ResetPasswordService implements ResetPasswordUseCase {
    private static final String OTP_PREFIX = "password-reset:otp:";
    private static final int MAX_ATTEMPTS = 5;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetChallengeRepository challengeRepository;
    private final SessionVersionRepository sessionVersionRepository;
    private final byte[] hmacSecret;

    public ResetPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                PasswordResetChallengeRepository challengeRepository,
                                SessionVersionRepository sessionVersionRepository,
                                @Value("${app.auth.reset-hmac-secret:${app.jwt.secret}}") String hmacSecret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.challengeRepository = challengeRepository;
        this.sessionVersionRepository = sessionVersionRepository;
        this.hmacSecret = hmacSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void resetPassword(ResetPasswordCommand command) {
        if (command.newPassword() == null || command.newPassword().length() < 6)
            throw new InvalidOperationException("Password must be at least 6 characters");
        if (command.otp() == null || command.otp().isBlank())
            throw new InvalidOperationException("Verification code is required");
        if (!command.otp().matches("\\d{6}"))
            throw invalidOtp();

        String email = ForgotPasswordService.normalize(command.email());
        String emailHash = hmac(email);
        PasswordResetChallengeRepository.ConsumeResult result = challengeRepository.consume(
                OTP_PREFIX + emailHash, hmac(command.otp()), MAX_ATTEMPTS);
        if (result.status() != PasswordResetChallengeRepository.ConsumeResult.Status.CONSUMED)
            throw invalidOtp();

        Long userId = result.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
        userRepository.updatePassword(user.getId(), passwordEncoder.encode(command.newPassword()));
        sessionVersionRepository.incrementVersion(user.getId());
    }

    private BookstoreException invalidOtp() {
        return new BookstoreException("Invalid or expired verification code", BookstoreException.UNAUTHORIZED);
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to hash reset value", e);
        }
    }
}
