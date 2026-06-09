package com.senla.ProductService.dto.price;

import java.time.LocalDate;
import java.util.UUID;

public record ProductPriceDTO(
        UUID id,
        UUID productId,
        String productName,
        String productDescription,
        Double productAmount,
        String productUnit,
        UUID productCategoryId,
        String productCategoryName,
        UUID shopBranchId,
        String shopAddress,
        String shopName,
        UUID cityId,
        String cityName,
        Double price,
        LocalDate startDate,
        Integer discountPercent,
        String status
) {
}
