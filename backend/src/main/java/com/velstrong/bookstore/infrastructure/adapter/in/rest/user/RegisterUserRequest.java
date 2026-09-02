package com.velstrong.bookstore.infrastructure.adapter.in.rest.user;

import com.velstrong.bookstore.application.command.user.RegisterUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 6) String password,
        @NotBlank @Email String email,
        String fullname
) {
    public RegisterUserCommand toCommand() {
        return new RegisterUserCommand(username, password, email, fullname);
    }
}
