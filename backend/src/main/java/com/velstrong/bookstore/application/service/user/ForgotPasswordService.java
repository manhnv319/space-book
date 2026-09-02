package com.velstrong.bookstore.application.service.user;

import com.velstrong.bookstore.application.command.user.ForgotPasswordCommand;
import com.velstrong.bookstore.domain.port.in.user.ForgotPasswordUseCase;
import com.velstrong.bookstore.domain.port.out.EmailServicePort;
import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.HexFormat;

@Service
@Transactional
public class ForgotPasswordService implements ForgotPasswordUseCase {
    static final Duration OTP_TTL = Duration.ofMinutes(15);
    static final int RATE_LIMIT = 3;
    private static final String OTP_PREFIX = "password-reset:otp:";
    private static final String RATE_PREFIX = "password-reset:rate:";

    private final UserRepository userRepository;
    private final EmailServicePort emailServicePort;
    private final PasswordResetChallengeRepository challengeRepository;
    private final SecureRandom random = new SecureRandom();
    private final byte[] hmacSecret;

    public ForgotPasswordService(UserRepository userRepository, EmailServicePort emailServicePort,
                                 PasswordResetChallengeRepository challengeRepository,
                                 @Value("${app.auth.reset-hmac-secret:${app.jwt.secret}}") String hmacSecret) {
        this.userRepository = userRepository;
        this.emailServicePort = emailServicePort;
        this.challengeRepository = challengeRepository;
        this.hmacSecret = hmacSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void forgotPassword(ForgotPasswordCommand command) {
        String email = normalize(command.email());
        String emailHash = hmac(email);
        try {
            if (!challengeRepository.allowRequest(RATE_PREFIX + emailHash, OTP_TTL, RATE_LIMIT)) return;
            userRepository.findByEmail(email).ifPresent(user -> {
                String otp = String.format("%06d", random.nextInt(1_000_000));
                challengeRepository.put(OTP_PREFIX + emailHash, hmac(otp), user.getId(), OTP_TTL);
                emailServicePort.sendPasswordResetEmail(user.getEmail(), otp);
            });
        } catch (RuntimeException ignored) {
            // Keep the public response generic. Operational telemetry must not contain email or OTP values.
        }
    }

    static String normalize(String email) { return email == null ? "" : email.trim().toLowerCase(); }

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
