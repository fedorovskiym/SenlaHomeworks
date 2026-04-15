package com.senla.ProductService.dto.productCategory;

import jakarta.validation.constraints.NotBlank;

public record ProductCategoryDTO(
        Long id,
        @NotBlank String name,
        @NotBlank String description,
        String imageUrl) {
}
