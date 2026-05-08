package com.senla.ProductService.dto.subscription;

import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionDTO(
        UUID id,
        UUID productId,
        String productName,
        UUID shopBranchId,
        String shopName,
        String shopAddress,
        Double price,
        Integer discountPercent,
        LocalDate startDate
) {
}
