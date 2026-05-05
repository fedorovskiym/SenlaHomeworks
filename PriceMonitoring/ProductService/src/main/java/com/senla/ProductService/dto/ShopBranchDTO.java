package com.senla.ProductService.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ShopBranchDTO(
        UUID id,
        @NotNull(message = "Shop id must be not null") UUID shopId,
        @NotNull(message = "City id must be not null") UUID cityId,
        String shopName,
        String cityName,
        @NotBlank(message = "Street must be not null") String street,
        @NotNull(message = "House number must be not null") Integer house,
        Integer room,
        String logoImageUrl) {
}
