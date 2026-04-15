package com.senla.ProductService.dto.product;

public record ProductUpdateDTO(
        String name,
        String description,
        Double amount,
        String unit,
        Long brandId,
        Long categoryId
) {
}
