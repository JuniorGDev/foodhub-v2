package br.com.foodhub.presentation.response;

public record UserResponse(
        String name,
        String email,
        String address,
        UserTypeResponse userType
) {
}
