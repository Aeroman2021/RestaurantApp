package com.restaurant.app.demo.model.dto.user;

import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

        @NotBlank(message = "username should not be blank")
        String userName,

        @NotBlank(message = "password should not be blank")
        String password,

        @NotBlank(message = "firstname should not be blank")
        String firstName,

        @NotBlank(message = "lastname should not be blank")
        String lastName,

        @NotBlank(message = "phone should not be blank")
        @Pattern(
                regexp = "^09\\d{9}$",
                message = "phone number must be a valid Iranian mobile number"
        )
        String phone,

        @NotBlank(message = "email should not be blank")
        @Email(message = "email should be valid")
        String email,

        @NotNull(message = "customerLevel should not be blank")
        CustomerLevel customerLevel
) {
}
