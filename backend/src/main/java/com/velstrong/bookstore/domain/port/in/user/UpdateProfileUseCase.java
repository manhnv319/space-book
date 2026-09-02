package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.UpdateProfileCommand;
import com.velstrong.bookstore.application.response.user.UserResponse;

public interface UpdateProfileUseCase {
    UserResponse updateProfile(UpdateProfileCommand command);
}
