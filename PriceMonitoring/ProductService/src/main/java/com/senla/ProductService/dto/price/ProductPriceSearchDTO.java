package com.senla.ProductService.dto.price;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProductPriceSearchDTO(
        @Min(value = 1, message = "Page number must be greater than 0") Integer page,
        @Min(value = 1, message = "Size number must be greater than 0") Integer size,
        @NotNull(message = "Shop branch's id must be not null") UUID shopBranchId,
        String sortBy,
        Boolean asc,
        @NotNull(message = "Brand's id must be not null") UUID brandId,
        @NotNull(message = "Category id must be not null") UUID categoryId
) {

    public ProductPriceSearchDTO {
        if(sortBy == null) sortBy = "id";
        if(asc == null) asc = true;
    }
}
