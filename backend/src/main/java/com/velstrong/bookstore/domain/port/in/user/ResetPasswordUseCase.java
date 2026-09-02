package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.ResetPasswordCommand;

public interface ResetPasswordUseCase {
    void resetPassword(ResetPasswordCommand command);
}
