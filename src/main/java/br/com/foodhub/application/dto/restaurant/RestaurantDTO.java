package br.com.foodhub.application.dto.restaurant;

import java.time.LocalTime;
import java.util.UUID;

public record RestaurantDTO(
        String name,
        String address,
        UUID kitchenTypeId,
        LocalTime openingTime,
        LocalTime closingTime,
        UUID ownerId
) {
}
