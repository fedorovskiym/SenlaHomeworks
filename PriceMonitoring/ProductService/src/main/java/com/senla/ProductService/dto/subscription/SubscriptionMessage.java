package com.senla.ProductService.dto.subscription;

import java.util.UUID;

public record SubscriptionMessage(
        UUID subscriptionId,
        UUID productPriceId,
        UUID productId,
        String productName,
        UUID shopBranchId,
        String shopName,
        UUID userId
) {
}
