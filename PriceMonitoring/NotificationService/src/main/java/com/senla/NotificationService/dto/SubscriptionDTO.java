package com.senla.NotificationService.dto;

public record SubscriptionDTO(
        Long productPriceId,
        Long productId,
        String productName,
        Long shopBranchId,
        String shopName,
        Long userId
) {
}
