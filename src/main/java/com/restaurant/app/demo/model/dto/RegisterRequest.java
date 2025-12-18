package com.restaurant.app.demo.model.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(

        @NotBlank
        String userName,

        @NotBlank
        String password,

         String firstName,

        String lastName,

        String phone,

        String email



) {
}
