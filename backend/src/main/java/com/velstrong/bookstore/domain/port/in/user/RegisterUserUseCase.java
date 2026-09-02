package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.RegisterUserCommand;
import com.velstrong.bookstore.application.response.user.UserResponse;

public interface RegisterUserUseCase {
    UserResponse register(RegisterUserCommand command);
}
