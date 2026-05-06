package com.senla.NotificationService.dto;

import java.util.UUID;

public record SubscriptionDTO(
        UUID subscriptionId,
        UUID productPriceId,
        UUID productId,
        String productName,
        UUID shopBranchId,
        String shopName,
        UUID userId
) {
}
