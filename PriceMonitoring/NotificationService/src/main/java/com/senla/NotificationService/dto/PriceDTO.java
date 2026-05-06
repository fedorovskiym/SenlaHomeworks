package com.senla.NotificationService.dto;

import java.util.UUID;

public record PriceDTO(
        UUID id,
        String productName,
        Double price,
        Integer discountPercent,
        UUID userId
) {
}
