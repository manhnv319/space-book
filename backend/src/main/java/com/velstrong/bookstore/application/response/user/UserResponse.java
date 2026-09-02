package com.velstrong.bookstore.application.response.user;

import com.velstrong.bookstore.domain.model.User;
import com.velstrong.bookstore.domain.model.enums.user.UserStatus;

import java.time.LocalDate;
import java.util.List;

public record UserResponse(
        Long id,
        String username,
        String email,
        String fullname,
        String phone,
        LocalDate birthday,
        Byte customerTierId,
        UserStatus status,
        List<String> roles,
        List<String> permissions
) {
    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getFullname(), user.getPhone(), user.getBirthday(),
                user.getCustomerTierId(), user.getStatus(), user.getRoles(), user.getScopes());
    }
}
