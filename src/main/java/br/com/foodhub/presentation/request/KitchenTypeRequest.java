package br.com.foodhub.presentation.request;

import br.com.foodhub.shared.constants.ValidationConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(
        name = "KitchenTypeRequest",
        description = "Request to create or update a kitchen type."
)
public record KitchenTypeRequest(
        @Schema(
                description = "Kitchen type name",
                example = "ITALIAN"
        )
        @NotBlank(message = "Name is required")
        @Size(
                min = ValidationConstants.NAME_MIN_LENGTH,
                max = ValidationConstants.NAME_MAX_LENGTH,
                message = "Name must be between 3 and 150 characters"
        )
        String name
) {
}
