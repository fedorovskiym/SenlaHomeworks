package com.senla.ProductService.dto.product;

import jakarta.validation.constraints.Min;

import java.util.UUID;

public record ProductSearchDTO(
        @Min(value = 1, message = "Page number must be greater than 0") Integer page,
        @Min(value = 1, message = "Size number must be greater than 0") Integer size,
        UUID brandId,
        UUID productCategoryId,
        String unit,
        String sortBy,
        Boolean asc
) {
    public ProductSearchDTO {
        if(sortBy == null) sortBy = "id";
        if(asc == null) asc = true;
    }
}
