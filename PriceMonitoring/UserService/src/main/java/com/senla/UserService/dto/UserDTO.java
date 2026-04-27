package com.senla.UserService.dto;

import java.time.LocalDate;

public record UserDTO(
        Long id,
        String username,
        String phoneNumber,
        LocalDate registrationDate
) {
}
