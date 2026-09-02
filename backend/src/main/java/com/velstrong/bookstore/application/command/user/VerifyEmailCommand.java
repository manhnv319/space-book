package com.velstrong.bookstore.application.command.user;

public record VerifyEmailCommand(String email, String otp) {}
