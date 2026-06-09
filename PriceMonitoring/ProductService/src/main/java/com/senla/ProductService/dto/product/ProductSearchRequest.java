package com.senla.ProductService.dto.product;

public record ProductSearchRequest(
        String categoryName,
        String brandName,
        String productName,
        String description
) {
}
