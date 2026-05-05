package com.senla.ProductService.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CityDTO(
        UUID id,
        @NotBlank(message = "City name must be not null") String name
) {
}
