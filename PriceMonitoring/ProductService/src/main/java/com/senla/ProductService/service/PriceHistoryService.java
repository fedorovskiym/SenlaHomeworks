package com.senla.ProductService.service;

import com.senla.ProductService.dto.history.PriceHistoryDTO;
import com.senla.ProductService.dto.history.PriceHistoryOverPeriodOfTimeDTO;
import com.senla.ProductService.model.PriceHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.UUID;

/**
 * interface for work with priceHistory
 */
public interface PriceHistoryService {

    /**
     * method for save priceHistory
     *
     * @param priceHistory contains price history data
     */
    void save(PriceHistory priceHistory);

    /**
     * method for savind list price history
     *
     * @param priceHistoryList contains list of price history
     */
    void saveList(List<PriceHistory> priceHistoryList);

    /**
     * gettiing coords for chart
     *
     * @param productId product id to find price history
     * @param shopBranchId shop branch id to find price history
     * @return PriceHistoryDTO with coords for building chart
     */
    PriceHistoryDTO getCoordsForChart(UUID productId, UUID shopBranchId);

    /**
     * method for generate csv with price history over period of time
     *
     * @param periodOfTimeDTO data with period of time and data for searching history
     * @return String in csv format
     */
    String generateCsv(PriceHistoryOverPeriodOfTimeDTO periodOfTimeDTO);
}
