package com.senla.NotificationService.repository;

import com.senla.NotificationService.model.Subscription;

import java.util.List;

public interface SubscriptionRepository extends GenericRepository<Subscription, Long> {
    List<Subscription> findByProductPriceId(Long id);
}
