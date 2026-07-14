package br.com.foodhub.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "MenuItemResponse",
        description = "Represents a menu item returned by the API."
)
public record MenuItemResponse(

        @Schema(
                description = "Unique identifier of the menu item.",
                example = "ffffffff-ffff-ffff-ffff-ffffffffffff"
        )
        UUID id,

        @Schema(
                description = "Menu item name.",
                example = "Cheeseburger"
        )
        String name,

        @Schema(
                description = "Detailed description of the menu item.",
                example = "Beef burger with cheddar cheese, lettuce and tomato."
        )
        String description,

        @Schema(
                description = "Menu item price.",
                example = "29.90"
        )
        BigDecimal price,

        @Schema(
                description = "Indicates whether the item is available only for consumption at the restaurant.",
                example = "true"
        )
        boolean availableOnlyInRestaurant,

        @Schema(
                description = "Image path or URL associated with the menu item.",
                example = "/images/menu-items/cheeseburger.png"
        )
        String imagePath

) {
}
