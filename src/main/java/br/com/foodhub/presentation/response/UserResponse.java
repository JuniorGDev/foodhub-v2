package br.com.foodhub.presentation.response;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String address,
        UserTypeResponse userType
) {
}
