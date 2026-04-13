package com.senla.ProductService.dto;

import jakarta.validation.constraints.NotBlank;

public record CityDTO(
        Long id,
        @NotBlank(message = "City name must be not null") String name) {
}
