package br.com.foodhub.presentation.request;

import br.com.foodhub.shared.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "MenuItemRequest",
        description = "Request used to create a new menu item."
)
public record MenuItemRequest(

        @Schema(
                description = "Menu item name.",
                example = "Cheeseburger"
        )
        @NotBlank(message = "Name is required")
        @Size(
                min = ValidationConstants.NAME_MIN_LENGTH,
                max = ValidationConstants.NAME_MAX_LENGTH,
                message = "Name must be between 3 and 100 characters"
        )
        String name,

        @Schema(
                description = "Detailed description of the menu item.",
                example = "Beef burger with cheddar cheese, lettuce and tomato."
        )
        @NotBlank(message = "Description is required")
        @Size(
                max = ValidationConstants.DESCRIPTION_MAX_LENGTH,
                message = "Description must have at most 250 characters"
        )
        String description,

        @Schema(
                description = "Price of the menu item.",
                example = "29.90"
        )
        @NotNull(message = "Price is required")
        @DecimalMin(
                value = "0.01",
                inclusive = true,
                message = "Price must be greater than zero"
        )
        BigDecimal price,

        @Schema(
                description = "Indicates whether the item is available only for consumption inside the restaurant.",
                example = "true"
        )
        boolean availableOnlyInRestaurant,

        @Schema(
                description = "Image path of the menu item.",
                example = "/images/menu-items/cheeseburger.png"
        )
        @NotBlank(message = "Image path is required")
        @Size(
                max = ValidationConstants.IMAGE_PATH_MAX_LENGTH,
                message = "Image path must have at most 255 characters"
        )
        String imagePath,

        @Schema(
                description = "Identifier of the restaurant that owns the menu item.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Restaurant is required")
        UUID restaurantId

) {
}
