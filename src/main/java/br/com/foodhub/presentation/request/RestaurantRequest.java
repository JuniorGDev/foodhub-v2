package br.com.foodhub.presentation.request;

import br.com.foodhub.shared.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.UUID;

@Schema(
        name = "RestaurantRequest",
        description = "Request used to create a new restaurant."
)
public record RestaurantRequest(

        @Schema(
                description = "Restaurant name.",
                example = "Pizzaria Napoli"
        )
        @NotBlank(message = "Name is required")
        @Size(
                min = ValidationConstants.NAME_MIN_LENGTH,
                max = ValidationConstants.NAME_MAX_LENGTH,
                message = "Name must be between 3 and 100 characters"
        )
        String name,

        @Schema(
                description = "Kitchen type identifier.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Kitchen type is required")
        UUID kitchenTypeId,

        @Schema(
                description = "Restaurant address.",
                example = "Av. Paulista, 1000 - São Paulo/SP"
        )
        @NotBlank(message = "Address is required")
        @Size(
                max = ValidationConstants.ADDRESS_MAX_LENGTH,
                message = "Address must have at most 255 characters"
        )
        String address,

        @Schema(
                description = "Restaurant opening time.",
                example = "08:00",
                type = "string",
                format = "time"
        )
        @NotNull(message = "Opening time is required")
        LocalTime openingTime,

        @Schema(
                description = "Restaurant closing time.",
                example = "22:00",
                type = "string",
                format = "time"
        )
        @NotNull(message = "Closing time is required")
        LocalTime closingTime,

        @Schema(
                description = "Owner identifier.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "Owner is required")
        UUID ownerId
) {
}