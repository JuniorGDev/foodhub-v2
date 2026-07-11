package br.com.foodhub.presentation.request;

import br.com.foodhub.shared.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(
        name = "UserTypeRequest",
        description = "Request used to create or update a user type."
)
public record UserTypeRequest(

        @Schema(
                description = "Name of the user type.",
                example = "OWNER",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        @NotBlank(message = "Name is required")
        @Size(
                min = ValidationConstants.NAME_MIN_LENGTH,
                max = ValidationConstants.NAME_MAX_LENGTH,
                message = "Name must be between 3 and 100 characters"
        )
        String name

) {
}
