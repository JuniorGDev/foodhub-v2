package br.com.foodhub.presentation.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(
        name = "UserResponse",
        description = "Represents a user returned by the API."
)
public record UserResponse(

        @Schema(
                description = "Unique identifier of the user.",
                example = "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
        )
        UUID id,

        @Schema(
                description = "User full name.",
                example = "Geová Junior"
        )
        String name,

        @Schema(
                description = "User email address.",
                example = "owner@foodhub.com"
        )
        String email,

        @Schema(
                description = "User address.",
                example = "123 Main Street, São Paulo - SP"
        )
        String address,

        UserTypeResponse userType

) {
}
