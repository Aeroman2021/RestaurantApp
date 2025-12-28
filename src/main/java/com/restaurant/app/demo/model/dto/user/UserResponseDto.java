package com.restaurant.app.demo.model.dto.user;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username
) {
}
