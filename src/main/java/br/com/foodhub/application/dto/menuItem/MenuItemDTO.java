package br.com.foodhub.application.dto.menuItem;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemDTO(
        String name,
        String description,
        BigDecimal price,
        boolean availableOnlyInRestaurant,
        String imagePath,
        UUID restaurantId
) {
}
