package com.senla.ProductService.dto.price;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Range;

public record UpdateProductPrice(
        @Min(value = 0, message = "Price must be positive") Double price,
        @Range(min = 0, max = 100, message = "Discount must be between 0 and 100") Integer discountPercent
) {
}
