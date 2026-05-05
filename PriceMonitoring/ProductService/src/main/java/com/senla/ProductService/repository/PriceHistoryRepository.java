package com.senla.ProductService.repository;

import com.senla.ProductService.model.PriceHistory;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PriceHistoryRepository extends GenericRepository<PriceHistory, UUID> {

    List<PriceHistory> findAllByProductIdAndShopBranchId(UUID productId, UUID shopBranchId);

    List<PriceHistory> findOverPeriodOfTime(UUID productId, UUID shopBranchId, LocalDate startDate, LocalDate endDate);
}
