package com.velstrong.bookstore.application.service.auth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velstrong.bookstore.application.command.auth.GetTokenCommand;
import com.velstrong.bookstore.application.response.auth.TokenResponse;
import com.velstrong.bookstore.domain.exception.BookstoreException;
import com.velstrong.bookstore.domain.exception.EntityNotFoundException;
import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.port.in.auth.GetTokenUseCase;
import com.velstrong.bookstore.domain.port.out.UserRepository;
import com.velstrong.bookstore.domain.port.out.JwtService;
import com.velstrong.bookstore.domain.port.out.PasswordEncoder;

@Service
@Transactional(readOnly = true)
public class GetTokenService implements GetTokenUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public GetTokenService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public TokenResponse getToken(GetTokenCommand command) {
        User user = userRepository.findByUsernameOrEmail(command.username().trim().toLowerCase())
                .orElseThrow(() -> new BookstoreException("Invalid credentials", BookstoreException.UNAUTHORIZED));

        if (!passwordEncoder.matches(command.password(), user.getPassword()))
            throw new BookstoreException("Invalid credentials", BookstoreException.UNAUTHORIZED);

        if (!user.isActive())
            throw new BookstoreException("Account is disabled", BookstoreException.FORBIDDEN);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return TokenResponse.of(accessToken, refreshToken, jwtService.getAccessTokenExpiry());
    }
}
