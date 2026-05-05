package com.senla.UserService.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserDTO(UUID id, String username, String phoneNumber, LocalDate registrationDate) {
}
