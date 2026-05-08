package com.senla.ProductService.service;

import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    void save(Subscription subscription);

    List<Subscription> findByProductPriceId(UUID id);

    void deleteByUserId(UUID id);

    List<Subscription> findByUserId(UUID id);

    List<SubscriptionDTO> findAll();

    Subscription findByIdIfExists(UUID id);

    void deleteById(UUID id);

    SubscriptionDetailsDTO findByIdWithDetails(UUID id);
}
