package br.com.foodhub.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "KitchenTypeResponse",
        description = "Represents a kitchen type returned by the API."
)
public record KitchenTypeResponse(
        @Schema(
                description = "Unique identifier of the kitchen type.",
                example = "44444444-4444-4444-4444-444444444444"
        )
        UUID id,
        @Schema(
                description = "Name of the kitchen type.",
                example = "ITALIAN"
        )
        String name
) {
}
