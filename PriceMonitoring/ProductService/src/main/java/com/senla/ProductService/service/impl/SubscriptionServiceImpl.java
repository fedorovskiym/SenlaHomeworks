package com.senla.ProductService.service.impl;

import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.repository.SubscriptionRepository;
import com.senla.ProductService.service.SubscriptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    @Transactional
    public void save(Subscription subscription) {
        logger.info("Save subscription {}", subscription);
        subscriptionRepository.save(subscription);
        logger.info("Subscription saved {}", subscription);
    }

    @Override
    public List<Subscription> findByProductPriceId(UUID id) {
        logger.info("Find all subscriptions by productPriceId {}", id);
        return subscriptionRepository.findByProductPriceId(id);
    }
}
