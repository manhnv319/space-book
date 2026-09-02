package com.velstrong.bookstore.infrastructure.adapter.in.rest.user;

import com.velstrong.bookstore.application.command.user.UpdateProfileCommand;

import java.time.LocalDate;

public record UpdateProfileRequest(String fullname, String phone, LocalDate birthday) {
    public UpdateProfileCommand toCommand(Long userId) {
        return new UpdateProfileCommand(userId, fullname, phone, birthday);
    }
}
