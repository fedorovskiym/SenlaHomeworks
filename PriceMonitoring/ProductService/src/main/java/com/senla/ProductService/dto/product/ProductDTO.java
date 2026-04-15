package com.senla.ProductService.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductDTO(
        Long id,
        @NotBlank(message = "Product name must be not null") String name,
        @NotBlank(message = "Product description must be not null") String description,
        @NotNull(message = "Product amount must be not null") Double amount,
        @NotBlank(message = "Product unit must be not null") String unit,
        @NotNull(message = "Product's brand id must be not null") Long brandId,
        String brandName,
        @NotNull(message = "Product's category id must be not null") Long categoryId,
        String categoryName,
        String imageUrl
) {
}
