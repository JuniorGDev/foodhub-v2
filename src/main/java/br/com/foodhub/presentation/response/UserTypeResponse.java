package br.com.foodhub.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "UserTypeResponse",
        description = "Represents a user type returned by the API."
)
public record UserTypeResponse(

        @Schema(
                description = "Unique identifier of the user type.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        UUID id,

        @Schema(
                description = "Name of the user type.",
                example = "OWNER"
        )
        String name

) {
}
