package com.senla.ProductService.dto;

public record SubscriptionMessage(
        Long productPriceId,
        Long productId,
        String productName,
        Long shopBranchId,
        String shopName,
        Long userId
) {
}
