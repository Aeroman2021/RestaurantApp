package com.restaurant.app.demo.model.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank
        String userName,

        @NotBlank
        String password

) {
}
