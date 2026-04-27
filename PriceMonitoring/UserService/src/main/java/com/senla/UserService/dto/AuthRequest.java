package com.senla.UserService.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @NotBlank(message = "Username must be not null!") String username,
        @NotBlank(message = "Password must be not null!") String password
) {
}
