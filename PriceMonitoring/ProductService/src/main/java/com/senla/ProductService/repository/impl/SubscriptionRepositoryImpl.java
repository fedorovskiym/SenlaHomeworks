package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Subscription;
import com.senla.ProductService.repository.SubscriptionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class SubscriptionRepositoryImpl extends AbstractGenericRepositoryImpl<Subscription, UUID> implements SubscriptionRepository {

    private static final String HQL_FIND_BY_PRODUCT_PRICE_ID = """
            SELECT s FROM Subscription s
            JOIN FETCH s.productPrice p
            WHERE p.id = :id
            """;

    private static final String HQL_FIND_BY_USER_ID = """
            SELECT s FROM Subscription s
            WHERE s.userId = :userId
            """;

    public SubscriptionRepositoryImpl() {
        super(Subscription.class);
    }

    @Override
    public List<Subscription> findByProductPriceId(UUID id) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PRODUCT_PRICE_ID, Subscription.class)
                .setParameter("id", id)
                .getResultList();
    }

    @Override
    public List<Subscription> findByUserId(UUID id) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_USER_ID, Subscription.class)
                .setParameter("userId", id)
                .getResultList();
    }
}
