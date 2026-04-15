package com.senla.ProductService.dto.brand;

import jakarta.validation.constraints.NotBlank;

public record BrandDTO(
        Long id,
        @NotBlank(message = "Name must be not null") String name,
        @NotBlank(message = "Country must be not null") String country,
        String logoImageUrl) {
}
