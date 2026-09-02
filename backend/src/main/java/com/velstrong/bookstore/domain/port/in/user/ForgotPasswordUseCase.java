package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.ForgotPasswordCommand;

public interface ForgotPasswordUseCase {
    void forgotPassword(ForgotPasswordCommand command);
}
