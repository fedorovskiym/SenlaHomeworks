package com.senla.NotificationService.repository;

import com.senla.NotificationService.model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionRepository extends GenericRepository<Subscription, UUID> {
    List<Subscription> findByProductPriceId(UUID id);
}
