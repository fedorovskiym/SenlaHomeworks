package com.senla.ProductService.dto.price;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ProductPriceSearchDTO(
        @Min(value = 1, message = "Page number must be greater than 0") Integer page,
        @Min(value = 1, message = "Size number must be greater than 0") Integer size,
        @NotNull @Min(1) @NotNull Long shopBranchId,
        String sortBy,
        Boolean asc,
        @Min(1) Long brandId,
        @Min(1) Long categoryId
) {

    public ProductPriceSearchDTO {
        if(sortBy == null) sortBy = "id";
        if(asc == null) asc = true;
    }
}
