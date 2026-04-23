package com.senla.ProductService.dto.history;

import java.time.LocalDate;

public record PriceHistoryDataDTO(
        Double oldPrice,
        Double newPrice,
        LocalDate changeDate
) {
}
