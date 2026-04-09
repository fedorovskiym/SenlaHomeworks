package com.senla.ProductService.dto;

import jakarta.validation.constraints.NotBlank;

public record ShopDTO(
        Long id,
        @NotBlank(message = "Name must be not null") String name,
        String logoImageUrl) {
}
