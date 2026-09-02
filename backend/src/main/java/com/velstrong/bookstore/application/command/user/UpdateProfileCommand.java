package com.velstrong.bookstore.application.command.user;

import java.time.LocalDate;

public record UpdateProfileCommand(Long userId, String fullname, String phone, LocalDate birthday) {}
