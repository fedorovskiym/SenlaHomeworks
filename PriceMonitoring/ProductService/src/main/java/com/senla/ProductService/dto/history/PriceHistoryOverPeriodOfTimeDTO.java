package com.senla.ProductService.dto.history;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PriceHistoryOverPeriodOfTimeDTO(
        @Min(1) Long productId,
        @Min(1) Long shopBranchId,
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd") @NotNull LocalDate endDate
) {
}
