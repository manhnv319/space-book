package com.velstrong.bookstore.application.service.auth;

import com.velstrong.bookstore.domain.port.in.auth.LogoutUseCase;
import com.velstrong.bookstore.domain.port.out.IamTokenRepository;
import com.velstrong.bookstore.domain.port.out.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogoutService implements LogoutUseCase {

    private final IamTokenRepository tokenRepository;
    private final JwtService jwtService;

    public LogoutService(IamTokenRepository tokenRepository, JwtService jwtService) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void logout(String accessToken, Long userId) {
        // F15: blacklist the access token in Redis with TTL = remaining lifetime
        // so the JwtAuthFilter rejects it on the next request until it expires.
        long remaining = Math.max(1, jwtService.getAccessTokenExpiry());
        tokenRepository.blacklistAccessToken(accessToken, remaining);
        tokenRepository.deleteAccessToken(String.valueOf(userId));
    }
}
