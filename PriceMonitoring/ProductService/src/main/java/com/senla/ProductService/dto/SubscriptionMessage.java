package com.senla.ProductService.dto;

import java.util.UUID;

public record SubscriptionMessage(
        UUID productPriceId,
        UUID productId,
        String productName,
        UUID shopBranchId,
        String shopName,
        UUID userId
) {
}
