package com.velstrong.bookstore.domain.port.in.auth;

import com.velstrong.bookstore.application.command.auth.GetTokenCommand;
import com.velstrong.bookstore.application.response.auth.TokenResponse;

public interface GetTokenUseCase {
    TokenResponse getToken(GetTokenCommand command);
}
