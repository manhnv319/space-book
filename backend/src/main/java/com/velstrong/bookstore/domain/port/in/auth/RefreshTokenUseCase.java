package com.velstrong.bookstore.domain.port.in.auth;

import com.velstrong.bookstore.application.command.auth.RefreshTokenCommand;
import com.velstrong.bookstore.application.response.auth.TokenResponse;

public interface RefreshTokenUseCase {
    TokenResponse refresh(RefreshTokenCommand command);
}
