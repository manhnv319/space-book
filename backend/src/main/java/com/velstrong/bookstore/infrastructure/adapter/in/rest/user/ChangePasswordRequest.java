package com.velstrong.bookstore.infrastructure.adapter.in.rest.user;

import com.velstrong.bookstore.application.command.user.ChangePasswordCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank @Size(min = 6) String newPassword) {
    public ChangePasswordCommand toCommand(Long userId) {
        return new ChangePasswordCommand(userId, currentPassword, newPassword);
    }
}
