package com.velstrong.bookstore.application.command.rental;

public record ReturnRentalCommand(Long rentalId, Long userId, Long damageFeeAmount, String notes) {}
