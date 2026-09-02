package com.velstrong.bookstore.application.command.user;

public record ResetPasswordCommand(String email, String otp, String newPassword) {}
