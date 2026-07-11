package br.com.foodhub.application.dto.user;

import java.util.UUID;

public record UpdateUserDTO(
        String name,
        String email,
        String address,
        UUID userTypeId
) {
}
