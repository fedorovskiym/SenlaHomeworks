package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.dto.SubscriptionDTO;
import com.senla.NotificationService.model.Subscription;
import com.senla.NotificationService.repository.SubscriptionRepository;
import org.springframework.stereotype.Repository;

@Repository
public class SubscriptionRepositoryImpl extends AbstractGenericRepositoryImpl<Subscription, Long> implements SubscriptionRepository {


    public SubscriptionRepositoryImpl() {
        super(Subscription.class);
    }
}
