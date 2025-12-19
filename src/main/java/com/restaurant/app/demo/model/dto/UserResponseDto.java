package com.restaurant.app.demo.model.dto;

public record UserResponseDto(
        Long id,
        String firstName,
        String lastName,
        String username
) {
}
