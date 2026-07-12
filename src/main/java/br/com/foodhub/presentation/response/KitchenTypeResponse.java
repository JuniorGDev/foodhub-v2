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
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,
        @Schema(
                description = "Name of the kitchen type.",
                example = "ITALIAN"
        )
        String name
) {
}
