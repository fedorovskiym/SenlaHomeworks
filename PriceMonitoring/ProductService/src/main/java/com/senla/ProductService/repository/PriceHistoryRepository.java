package com.senla.ProductService.repository;

import com.senla.ProductService.model.PriceHistory;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.List;

public interface PriceHistoryRepository extends GenericRepository<PriceHistory, Long> {

    List<PriceHistory> findAllByProductIdAndShopBranchId(Long productId, Long shopBranchId);

    List<PriceHistory> findOverPeriodOfTime(Long productId, Long shopBranchId, LocalDate startDate, LocalDate endDate);
}
