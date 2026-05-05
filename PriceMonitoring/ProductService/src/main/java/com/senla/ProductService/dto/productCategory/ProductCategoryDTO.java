package com.senla.ProductService.dto.productCategory;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ProductCategoryDTO(
        UUID id,
        @NotBlank String name,
        @NotBlank String description,
        String imageUrl) {
}
