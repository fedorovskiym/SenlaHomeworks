package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.mapper.SubscriptionMapper;
import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.repository.SubscriptionRepository;
import com.senla.ProductService.service.ProductPriceService;
import com.senla.ProductService.service.SubscriptionService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;
    private static final Logger logger = LoggerFactory.getLogger(SubscriptionServiceImpl.class);

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository,
                                   SubscriptionMapper subscriptionMapper) {
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionMapper = subscriptionMapper;
    }

    @Override
    @Transactional
    public void save(Subscription subscription) {
        if (!subscriptionRepository.findByUserIdAndProductPriceId(subscription.getUserId(),
                subscription.getProductPrice().getId())) {
            throw new EntityExistsException("Subscription already exists!");
        }
        logger.info("Save subscription {}", subscription);
        subscriptionRepository.save(subscription);
        logger.info("Subscription saved {}", subscription);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> findByProductPriceId(UUID id) {
        logger.info("Find all subscriptions by productPriceId {}", id);
        return subscriptionRepository.findByProductPriceId(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID id) {
        logger.info("Delete subscriptions by user id {}", id);
        List<Subscription> subscriptions = findByUserId(id);
        subscriptions.forEach(subscriptionRepository::delete);
        logger.info("{} subscriptions deleted by user id {}", subscriptions.size(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> findByUserId(UUID id) {
        return subscriptionRepository.findByUserId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionDTO> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = (UUID) authentication.getDetails();
        logger.info("Find all subscription with user id {}", userId);
        return subscriptionRepository.findAllByUserId(userId).stream()
                .map(subscriptionMapper::subscriptionToSubscriptionDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Subscription findByIdIfExists(UUID id) {
        return subscriptionRepository.findById(id).orElseThrow(() -> {
            logger.warn("Subscription with id {} not found", id);
            return new EntityNotFoundException("Subscription with id " + id + " not found");
        });
    }

    @Override
    public Subscription findByIdWithFetch(UUID id) {
        return subscriptionRepository.findByIdWithFetch(id).orElseThrow(() -> {
            logger.warn("Subscription with id {} not found", id);
            return new EntityNotFoundException("Subscription with id " + id + " not found");
        });
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        Subscription subscription = findByIdIfExists(id);
        subscriptionRepository.delete(subscription);
        logger.info("Subscription with id {} deleted", id);
    }
}
