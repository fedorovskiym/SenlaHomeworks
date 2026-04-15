package com.senla.ProductService.dto.price;

import java.time.LocalDate;

public record ProductPriceDTO(
        Long id,
        Long productId,
        String productName,
        Double productAmount,
        String productUnit,
        Long shopBranchId,
        String shopName,
        Double price,
        LocalDate startDate,
        Integer discountPercent
) {
}
