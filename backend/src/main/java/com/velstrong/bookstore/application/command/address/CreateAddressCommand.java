package com.velstrong.bookstore.application.command.address;

public record CreateAddressCommand(
        Long userId,
        String fullName,
        String phone,
        String province,
        String district,
        String ward,
        String addressDetail,
        boolean isDefault
) {}
