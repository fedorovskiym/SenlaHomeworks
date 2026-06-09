package com.senla.ProductService.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record ShopDTO(
        UUID id,
        @NotBlank(message = "Name must be not null") String name,
        String logoImageUrl) {
}
