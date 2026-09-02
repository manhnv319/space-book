package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.ChangePasswordCommand;

public interface ChangePasswordUseCase {
    void changePassword(ChangePasswordCommand command);
}
