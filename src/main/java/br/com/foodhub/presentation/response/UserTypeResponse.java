package br.com.foodhub.presentation.response;

import java.util.UUID;

public record UserTypeResponse(
        UUID id,
        String name
) {
}
