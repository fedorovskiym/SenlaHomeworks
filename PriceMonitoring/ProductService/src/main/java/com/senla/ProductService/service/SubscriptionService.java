package com.senla.ProductService.service;

import com.senla.ProductService.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    void save(Subscription subscription);

    List<Subscription> findByProductPriceId(UUID id);
}
