package com.senla.ProductService.service;

import com.senla.ProductService.dto.subscription.SubscriptionDTO;
import com.senla.ProductService.dto.subscription.SubscriptionDetailsDTO;
import com.senla.ProductService.model.Subscription;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * interface for work with subscriptions
 */
public interface SubscriptionService {

    /**
     * method for saving subscription from data
     *
     * @param subscription contains data for saving subscription
     * @throws EntityExistsException if subscription exists
     */
    void save(Subscription subscription);

    /**
     * method for finding subscription by product price id
     *
     * @param id product price id to find subscriptions
     * @return list subscription
     */
    List<Subscription> findByProductPriceId(UUID id);

    /**
     * method for deleting subscription by user id
     *
     * @param id user id to delete subscriptions
     */
    void deleteByUserId(UUID id);

    /**
     * method for find list subscription by user id
     *
     * @param id user id to find subscriptions
     * @return list subscriptions with user id from request
     */
    List<Subscription> findByUserId(UUID id);

    /**
     * method for finding all user's subscriptions
     *
     * @return list subscriptionDTO mapped from list subcription
     */
    List<SubscriptionDTO> findAll();

    /**
     * method for finding subscription by id
     *
     * @param id from request to find subscription
     * @return subscription with id from request
     * @throws EntityNotFoundException if subscription with id from request not found
     */
    Subscription findByIdIfExists(UUID id);

    /**
     * method for finding subscription with fetch entities
     *
     * @param id from request to find subscription with entities
     * @return subscription with entities
     * @throws EntityNotFoundException if subscription with id from request not found
     */
    Subscription findByIdWithFetch(UUID id);

    /**
     * method for deleting subscription by id
     *
     * @param id from request to delete subscription
     */
    void deleteById(UUID id);
}
