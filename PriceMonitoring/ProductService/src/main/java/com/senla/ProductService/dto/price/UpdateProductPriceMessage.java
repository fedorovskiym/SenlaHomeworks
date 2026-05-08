package com.senla.ProductService.dto.price;

import java.util.UUID;

public record UpdateProductPriceMessage(
        UUID id,
        String productName,
        Double price,
        Integer discountPercent,
        UUID userId
) {
}
