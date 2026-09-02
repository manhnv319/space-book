package com.velstrong.bookstore.application.command.user;

public record ChangePasswordCommand(Long userId, String currentPassword, String newPassword) {}
