package com.senla.UserService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(
        @NotBlank(message = "Username must be not null!") String username,
        @Pattern(regexp = "^7([0-9]{10})$", message = "Phone number format is '79999999999'") String phoneNumber,
        @NotBlank(message = "Password must be not null!") String password
) {
}
