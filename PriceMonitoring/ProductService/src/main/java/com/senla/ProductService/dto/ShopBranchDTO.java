package com.senla.ProductService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ShopBranchDTO(
        Long id,
        @NotNull(message = "Shop id must be not null") Long shopId,
        @NotNull(message = "City id must be not null") Long cityId,
        String shopName,
        String cityName,
        @NotBlank(message = "Street must be not null") String street,
        @NotNull(message = "House number must be not null") Integer house,
        Integer room,
        String logoImageUrl) {
}
