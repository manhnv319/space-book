package com.velstrong.bookstore.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.auth.RefreshTokenCommand;
import com.velstrong.bookstore.application.response.auth.TokenResponse;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.auth.RefreshTokenUseCase;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.port.out.JwtService;
import com.velstrong.bookstore.domain.port.out.SessionVersionRepository;


@Service
@Transactional
public class RefreshTokenService implements RefreshTokenUseCase {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final SessionVersionRepository sessionVersionRepository;

    public RefreshTokenService(JwtService jwtService, UserRepository userRepository,
                               SessionVersionRepository sessionVersionRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.sessionVersionRepository = sessionVersionRepository;
    }

    @Override
    public TokenResponse refresh(RefreshTokenCommand command) {
        if (!jwtService.isTokenValid(command.refreshToken()))
            throw new BookstoreException("Invalid refresh token", BookstoreException.UNAUTHORIZED);

        Long userId = jwtService.extractUserId(command.refreshToken());
        if (jwtService.extractSessionVersion(command.refreshToken()) != sessionVersionRepository.currentVersion(userId))
            throw new BookstoreException("Invalid refresh token", BookstoreException.UNAUTHORIZED);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.of(newAccessToken, newRefreshToken, jwtService.getAccessTokenExpiry());
    }
}
