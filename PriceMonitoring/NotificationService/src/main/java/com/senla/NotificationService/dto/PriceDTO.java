package com.senla.NotificationService.dto;

import java.util.UUID;

public record PriceDTO(
        UUID id,
        Double price,
        Integer discountPercent
) {
}
