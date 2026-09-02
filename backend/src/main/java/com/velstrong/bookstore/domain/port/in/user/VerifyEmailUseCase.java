package com.velstrong.bookstore.domain.port.in.user;

import com.velstrong.bookstore.application.command.user.VerifyEmailCommand;

public interface VerifyEmailUseCase {
    void verifyEmail(VerifyEmailCommand command);
}
