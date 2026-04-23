package com.senla.ProductService.service;

import com.senla.ProductService.model.PriceHistory;

import java.util.List;

public interface PriceHistoryService {

    void save(PriceHistory priceHistory);

    void saveList(List<PriceHistory> priceHistoryList);
}
