package br.com.foodhub.presentation.request;

import java.util.UUID;

import br.com.foodhub.shared.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "UserRequest",
        description = "Request used to create a new user."
)
public record UserRequest(

        @Schema(
                description = "User full name.",
                example = "Geová Junior"
        )
        @NotBlank(message = "Name is required")
        @Size(
                min = ValidationConstants.NAME_MIN_LENGTH,
                max = ValidationConstants.NAME_MAX_LENGTH,
                message = "Name must be between 3 and 100 characters"
        )
        String name,

        @Schema(
                description = "User email address.",
                example = "junior@foodhub.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        @Size(
                max = ValidationConstants.EMAIL_MAX_LENGTH,
                message = "Email must have at most 150 characters"
        )
        String email,

        @Schema(
                description = "User password.",
                example = "StrongPassword123"
        )
        @NotBlank(message = "Password is required")
        @Size(
                min = ValidationConstants.PASSWORD_MIN_LENGTH,
                max = ValidationConstants.PASSWORD_MAX_LENGTH,
                message = "Password must be between 8 and 255 characters"
        )
        String password,

        @Schema(
                description = "User address.",
                example = "123 Main Street, São Paulo - SP"
        )
        @NotBlank(message = "Address is required")
        @Size(
                max = ValidationConstants.ADDRESS_MAX_LENGTH,
                message = "Address must have at most 255 characters"
        )
        String address,

        @Schema(
                description = "Identifier of the user type.",
                example = "550e8400-e29b-41d4-a716-446655440000"
        )
        @NotNull(message = "User type is required")
        UUID userTypeId

) {
}
