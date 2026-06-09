package com.senla.ProductService.repository;

import com.senla.ProductService.model.Subscription;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends GenericRepository<Subscription, UUID> {
    List<Subscription> findByProductPriceId(UUID id);

    List<Subscription> findByUserId(UUID id);

    List<Subscription> findAllByUserId(UUID id);

    boolean findByUserIdAndProductPriceId(UUID userId, UUID id);

    Optional<Subscription> findByIdWithFetch(UUID id);
}
