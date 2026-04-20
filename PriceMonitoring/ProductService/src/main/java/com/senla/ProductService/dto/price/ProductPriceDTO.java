package com.senla.ProductService.dto.price;

import java.time.LocalDate;

public record ProductPriceDTO(
        Long id,
        Long productId,
        String productName,
        Double productAmount,
        String productUnit,
        Long productCategoryId,
        String productCategoryName,
        Long shopBranchId,
        String shopAddress,
        String shopName,
        Long cityId,
        String cityName,
        Double price,
        LocalDate startDate,
        Integer discountPercent,
        String status
) {
}
