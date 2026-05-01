package com.senla.NotificationService.repository.impl;

import com.senla.NotificationService.model.Subscription;
import com.senla.NotificationService.repository.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionRepositoryImpl extends AbstractGenericRepositoryImpl<Subscription, Long> implements SubscriptionRepository {

    private static final String HQL_FIND_ALL_BY_PRODUCT_PRICE_ID = """
            SELECT s FROM Subscription s
            JOIN FETCH s.user
            WHERE s.productPriceId = :productPriceId
            """;

    public SubscriptionRepositoryImpl() {
        super(Subscription.class);
    }

    @Override
    public List<Subscription> findByProductPriceId(Long id) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_BY_PRODUCT_PRICE_ID, Subscription.class)
                .setParameter("productPriceId", id)
                .getResultList();
    }
}
