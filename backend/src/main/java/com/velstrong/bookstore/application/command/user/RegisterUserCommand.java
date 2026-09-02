package com.velstrong.bookstore.application.command.user;

public record RegisterUserCommand(String username, String password, String email, String fullname) {}
