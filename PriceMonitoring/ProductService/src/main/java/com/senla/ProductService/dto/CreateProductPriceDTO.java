package com.senla.ProductService.dto;

public record CreateProductPriceDTO(
        Long productId,
        Long shopBranchId,
        Double price,
        Integer discountPercent
) {
}
