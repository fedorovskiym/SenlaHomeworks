package com.senla.ProductService.repository;

import com.senla.ProductService.model.PriceHistory;

import java.util.List;

public interface PriceHistoryRepository extends GenericRepository<PriceHistory, Long> {

    List<PriceHistory> findAllByProductIdAndShopBranchId(Long productId, Long shopBranchId);
}
