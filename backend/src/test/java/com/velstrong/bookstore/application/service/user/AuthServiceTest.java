package com.velstrong.bookstore.application.service.user;

import com.velstrong.bookstore.application.command.user.ForgotPasswordCommand;
import com.velstrong.bookstore.application.command.user.ResetPasswordCommand;
import com.velstrong.bookstore.application.service.auth.LogoutService;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;
import com.velstrong.bookstore.domain.port.out.EmailServicePort;
import com.velstrong.bookstore.domain.port.out.IamTokenRepository;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.port.out.JwtService;
import com.velstrong.bookstore.domain.port.out.PasswordEncoder;
import com.velstrong.bookstore.domain.port.out.PasswordResetChallengeRepository;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private IamTokenRepository tokenRepository;
    private JwtService jwtService;
    private UserRepository userRepository;
    private EmailServicePort emailServicePort;
    private PasswordResetChallengeRepository challengeRepository;
    private SessionVersionRepository sessionVersionRepository;
    private PasswordEncoder passwordEncoder;
    private LogoutService logoutService;
    private ForgotPasswordService forgotService;
    private ResetPasswordService resetService;

    @BeforeEach
    void setUp() {
        tokenRepository = mock(IamTokenRepository.class);
        jwtService = mock(JwtService.class);
        userRepository = mock(UserRepository.class);
        emailServicePort = mock(EmailServicePort.class);
        challengeRepository = mock(PasswordResetChallengeRepository.class);
        sessionVersionRepository = mock(SessionVersionRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        logoutService = new LogoutService(tokenRepository, jwtService);
        forgotService = new ForgotPasswordService(userRepository, emailServicePort, challengeRepository, "test-secret");
        resetService = new ResetPasswordService(userRepository, passwordEncoder, challengeRepository, sessionVersionRepository, "test-secret");
    }

    @Test
    @DisplayName("logout blacklists the access token and deletes the saved one")
    void logoutBlacklistsToken() {
        when(jwtService.getAccessTokenExpiry()).thenReturn(3600L);

        logoutService.logout("ACCESS-1", 7L);

        ArgumentCaptor<Long> expiry = ArgumentCaptor.forClass(Long.class);
        verify(tokenRepository).blacklistAccessToken(anyString(), expiry.capture());
        assertThat(expiry.getValue()).isEqualTo(3600L);
        verify(tokenRepository).deleteAccessToken("7");
    }

    @Test
    @DisplayName("forgotPassword generates a six digit OTP, stores it with 15min TTL, sends email")
    void forgotPasswordSendsOtp() {
        User user = User.reconstitute(7L, "u", "hash", "u@x",
                null, null, null, null, null,
                UserStatus.ACTIVE, java.util.List.of(), java.util.List.of());
        when(userRepository.findByEmail("u@x")).thenReturn(Optional.of(user));
        when(challengeRepository.allowRequest(anyString(), any(Duration.class), any(Integer.class))).thenReturn(true);

        forgotService.forgotPassword(new ForgotPasswordCommand("u@x"));

        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> ttlCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(challengeRepository).put(anyString(), anyString(), anyLong(), ttlCaptor.capture());
        verify(emailServicePort).sendPasswordResetEmail(anyString(), otpCaptor.capture());
        assertThat(otpCaptor.getValue()).matches("\\d{6}");
        assertThat(ttlCaptor.getValue()).isEqualTo(Duration.ofMinutes(15));
    }

    @Test
    @DisplayName("forgotPassword is a no-op for unknown emails (no enumeration)")
    void forgotPasswordUnknownEmailIsNoOp() {
        when(userRepository.findByEmail("unknown@x")).thenReturn(Optional.empty());

        forgotService.forgotPassword(new ForgotPasswordCommand("unknown@x"));

        when(challengeRepository.allowRequest(anyString(), any(Duration.class), any(Integer.class))).thenReturn(true);
        verify(emailServicePort, never()).sendPasswordResetEmail(anyString(), anyString());
        verify(challengeRepository, never()).put(anyString(), anyString(), anyLong(), any(Duration.class));
    }

    @Test
    @DisplayName("resetPassword updates the password and consumes the reset token")
    void resetPasswordHappyPath() {
        User user = User.reconstitute(7L, "u", "oldHash", "u@x",
                null, null, null, null, null,
                UserStatus.ACTIVE, java.util.List.of(), java.util.List.of());
        when(challengeRepository.consume(anyString(), anyString(), any(Integer.class)))
                .thenReturn(new PasswordResetChallengeRepository.ConsumeResult(PasswordResetChallengeRepository.ConsumeResult.Status.CONSUMED, 7L));
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("newHash");

        resetService.resetPassword(new ResetPasswordCommand("u@x", "123456", "newpass"));

        verify(userRepository).updatePassword(7L, "newHash");
        verify(sessionVersionRepository).incrementVersion(7L);
    }

    @Test
    @DisplayName("resetPassword rejects when token missing or invalid")
    void resetPasswordMissingToken() {
        when(challengeRepository.consume(anyString(), anyString(), any(Integer.class)))
                .thenReturn(new PasswordResetChallengeRepository.ConsumeResult(PasswordResetChallengeRepository.ConsumeResult.Status.EXPIRED, null));

        assertThatThrownBy(() -> resetService.resetPassword(new ResetPasswordCommand("u@x", "123456", "newpass")))
                .isInstanceOf(BookstoreException.class)
                .hasMessageContaining("Invalid or expired");
    }

    @Test
    @DisplayName("resetPassword rejects short passwords")
    void resetPasswordShortPassword() {
        assertThatThrownBy(() -> resetService.resetPassword(new ResetPasswordCommand("u@x", "123456", "123")))
                .isInstanceOf(com.velstrong.bookstore.domain.exception.InvalidOperationException.class);
        verify(userRepository, never()).updatePassword(anyLong(), anyString());
    }

    @Test
    @DisplayName("resetPassword rejects blank token")
    void resetPasswordBlankToken() {
        assertThatThrownBy(() -> resetService.resetPassword(new ResetPasswordCommand("u@x", "", "newpass")))
                .isInstanceOf(com.velstrong.bookstore.domain.exception.InvalidOperationException.class);
        verify(challengeRepository, never()).consume(anyString(), anyString(), any(Integer.class));
    }
}
