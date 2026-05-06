package com.senla.ProductService.repository;

import com.senla.ProductService.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends GenericRepository<Subscription, UUID> {
    List<Subscription> findByProductPriceId(UUID id);
}
