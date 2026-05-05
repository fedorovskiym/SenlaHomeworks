package com.senla.ProductService.dto.brand;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record BrandDTO(
        UUID id,
        @NotBlank(message = "Name must be not null") String name,
        @NotBlank(message = "Country must be not null") String country,
        String logoImageUrl) {
}
