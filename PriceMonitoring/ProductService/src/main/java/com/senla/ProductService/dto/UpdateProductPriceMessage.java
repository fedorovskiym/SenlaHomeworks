package com.senla.ProductService.dto;

import java.util.UUID;

public record UpdateProductPriceMessage(
        UUID id,
        Double price,
        Integer discountPercent
) {
}
