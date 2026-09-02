package com.velstrong.bookstore.domain.port.in.auth;

public interface LogoutUseCase {
    void logout(String accessToken, Long userId);
}
