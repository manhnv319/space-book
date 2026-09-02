package com.velstrong.bookstore.application.command.rental;

public record ForceReturnRentalCommand(Long rentalId, Long damageFeeAmount, String notes) {}
