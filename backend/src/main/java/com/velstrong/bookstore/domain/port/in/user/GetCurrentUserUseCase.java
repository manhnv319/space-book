package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.response.user.UserResponse;

public interface GetCurrentUserUseCase {
    UserResponse getCurrentUser(Long userId);
}
