package com.senla.ProductService.dto.history;

import java.util.List;
import java.util.UUID;

public record PriceHistoryDTO(
        UUID id,
        UUID productId,
        String productName,
        UUID shopBranchId,
        String shopBranchName,
        List<PriceHistoryDataDTO> prices
) {
}
