package com.senla.ProductService.dto;

public record UpdateProductPriceMessage(
        Long id,
        Double price,
        Integer discountPercent
) {
}
