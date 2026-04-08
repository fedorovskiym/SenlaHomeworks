package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class PriceHistoryRepositoryImpl extends AbstractGenericRepositoryImpl<PriceHistory, Long> implements PriceHistoryRepository {

    public PriceHistoryRepositoryImpl() {
        super(PriceHistory.class);
    }
}
