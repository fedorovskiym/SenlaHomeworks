package com.senla.ProductService.service.impl;

import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import com.senla.ProductService.service.PriceHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceHistoryServiceImpl implements PriceHistoryService {

    private final PriceHistoryRepository priceHistoryRepository;

    @Autowired
    public PriceHistoryServiceImpl(PriceHistoryRepository priceHistoryRepository) {
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional
    public void save(PriceHistory priceHistory) {
        priceHistoryRepository.save(priceHistory);
    }

    @Override
    @Transactional
    public void saveList(List<PriceHistory> priceHistoryList) {
        priceHistoryList.forEach(priceHistoryRepository::save);
    }
}
