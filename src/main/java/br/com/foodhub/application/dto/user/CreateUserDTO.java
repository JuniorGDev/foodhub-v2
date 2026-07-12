package br.com.foodhub.application.dto.user;

import java.util.UUID;

public record CreateUserDTO(
        String name,
        String email,
        String password,
        String address,
        UUID userTypeId
) {
}
