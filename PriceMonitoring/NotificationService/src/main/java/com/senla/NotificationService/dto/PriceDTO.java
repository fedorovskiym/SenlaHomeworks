package com.senla.NotificationService.dto;

public record PriceDTO(
        Long id,
        Double price,
        Integer discountPercent
) {
}
