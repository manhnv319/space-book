package com.velstrong.bookstore.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.user.RegisterUserCommand;
import com.velstrong.bookstore.application.response.user.UserResponse;
import com.velstrong.bookstore.domain.exception.DuplicateEntityException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.user.RegisterUserUseCase;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.port.out.PasswordEncoder;
import com.velstrong.bookstore.domain.port.out.EmailServicePort;
import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;


@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailServicePort emailServicePort;
    private final PasswordResetChallengeRepository challengeRepository;
    private final byte[] hmacSecret;
    private final SecureRandom random = new SecureRandom();

    public RegisterUserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                               EmailServicePort emailServicePort,
                               PasswordResetChallengeRepository challengeRepository,
                               @Value("${app.auth.reset-hmac-secret:${app.jwt.secret}}") String hmacSecret) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailServicePort = emailServicePort;
        this.challengeRepository = challengeRepository;
        this.hmacSecret = hmacSecret.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public UserResponse register(RegisterUserCommand command) {
        if (userRepository.existsByUsername(command.username()))
            throw new DuplicateEntityException("User", "username", command.username());
        if (userRepository.existsByEmail(command.email()))
            throw new DuplicateEntityException("User", "email", command.email());

        User user = User.create(command.username(),
                passwordEncoder.encode(command.password()),
                command.email(), command.fullname());
        user.markPendingVerification();
        User saved = userRepository.save(user);
        String otp = String.format("%06d", random.nextInt(1_000_000));
        String emailHash = hmac(ForgotPasswordService.normalize(command.email()));
        challengeRepository.put("email-verification:otp:" + emailHash, hmac(otp), saved.getId(), Duration.ofMinutes(15));
        emailServicePort.sendEmailVerificationEmail(saved.getEmail(), otp, saved.getUsername());
        return UserResponse.from(saved);
    }

    private String hmac(String value) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacSecret, "HmacSHA256"));
            return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { throw new IllegalStateException("Unable to hash verification value", e); }
    }
}
