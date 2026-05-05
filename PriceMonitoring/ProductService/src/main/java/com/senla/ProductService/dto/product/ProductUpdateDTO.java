package com.senla.ProductService.dto.product;

import java.util.UUID;

public record ProductUpdateDTO(
        String name,
        String description,
        Double amount,
        String unit,
        UUID brandId,
        UUID categoryId
) {
}
