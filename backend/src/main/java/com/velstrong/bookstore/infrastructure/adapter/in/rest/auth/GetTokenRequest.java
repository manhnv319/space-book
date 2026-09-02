package com.velstrong.bookstore.infrastructure.adapter.in.rest.auth;

import jakarta.validation.constraints.NotBlank;

public record GetTokenRequest(@NotBlank String username, @NotBlank String password) {}
