package com.senla.ProductService.service;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.model.PriceHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface PriceHistoryService {

    void save(PriceHistory priceHistory);

    void saveList(List<PriceHistory> priceHistoryList);

    PriceHistoryDTO getCoordsForChart(Long productId, Long shopBranchId);

    String generateCsv(PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO);
}
