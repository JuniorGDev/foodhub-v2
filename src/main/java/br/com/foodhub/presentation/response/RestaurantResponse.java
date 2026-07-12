package br.com.foodhub.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@Schema(
        name = "RestaurantResponse",
        description = "Represents a restaurant returned by the API."
)
public record RestaurantResponse(

        @Schema(
                description = "Unique identifier of the restaurant.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Restaurant name.",
                example = "Pizzaria Napoli"
        )
        String name,

        @Schema(
                description = "Restaurant address.",
                example = "Av. Paulista, 1000 - São Paulo/SP"
        )
        String address,

        @Schema(
                description = "Kitchen type.",
                example = "ITALIAN"
        )
        String kitchenType,

        @Schema(
                description = "Restaurant opening time.",
                example = "08:00"
        )
        LocalTime openingTime,

        @Schema(
                description = "Restaurant closing time.",
                example = "22:00"
        )
        LocalTime closingTime,

        @Schema(
                description = "Restaurant owner's name.",
                example = "Geová Junior"
        )
        String ownerName,

        @Schema(
                description = "Restaurant menu."
        )
        List<MenuItemResponse> menuItems

) {
}
