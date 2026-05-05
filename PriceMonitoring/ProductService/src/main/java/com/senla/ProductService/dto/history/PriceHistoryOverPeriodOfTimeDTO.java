package com.senla.ProductService.dto.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record PriceHistoryOverPeriodOfTimeDTO(
        @NotNull(message = "Product's id must be not null") UUID productId,
        @NotNull(message = "Shop branch's id must be not null") UUID shopBranchId,
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate endDate
) {
}
