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
                example = "22222222-2222-2222-2222-222222222222"
        )
        UUID id,

        @Schema(
                description = "Name of the user type.",
                example = "OWNER"
        )
        String name

) {
}
