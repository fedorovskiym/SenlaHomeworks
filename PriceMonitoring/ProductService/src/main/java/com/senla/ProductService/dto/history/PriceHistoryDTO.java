package com.senla.ProductService.dto.history;

import java.util.List;

public record PriceHistoryDTO(
        Long id,
        Long productId,
        String productName,
        Long shopBranchId,
        String shopBranchName,
        List<PriceHistoryDataDTO> prices
) {
}
