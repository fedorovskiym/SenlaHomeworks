package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class PriceHistoryRepositoryImpl extends AbstractGenericRepositoryImpl<PriceHistory, UUID> implements PriceHistoryRepository {

    private static final String HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID = """
            SELECT ph FROM PriceHistory ph
            JOIN FETCH ph.product p
            JOIN FETCH ph.shopBranch sb
            JOIN FETCH sb.shop
            WHERE p.id = :productId AND sb.id = :shopBranchId
            ORDER BY ph.changeDate
            """;

    private static final String HQL_FIND_OVER_PERIOD_OF_TIME = """
            SELECT ph FROM PriceHistory ph
            JOIN FETCH ph.product p
            JOIN FETCH ph.shopBranch sb
            WHERE p.id = :productId AND sb.id = :shopBranchId
            AND ph.changeDate BETWEEN :startDate AND :endDate
            """;

    public PriceHistoryRepositoryImpl() {
        super(PriceHistory.class);
    }

    @Override
    public List<PriceHistory> findAllByProductIdAndShopBranchId(UUID productId, UUID shopBranchId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("shopBranchId", shopBranchId)
                .getResultList();
    }

    @Override
    public List<PriceHistory> findOverPeriodOfTime(UUID productId, UUID shopBranchId,
                                                   LocalDate startDate, LocalDate endDate) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_OVER_PERIOD_OF_TIME, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("shopBranchId", shopBranchId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }
}
