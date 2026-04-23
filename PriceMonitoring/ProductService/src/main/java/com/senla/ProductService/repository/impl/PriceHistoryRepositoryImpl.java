package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.PriceHistory;
import com.senla.ProductService.repository.PriceHistoryRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PriceHistoryRepositoryImpl extends AbstractGenericRepositoryImpl<PriceHistory, Long> implements PriceHistoryRepository {

    private static final String HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID = """
            SELECT ph FROM PriceHistory ph
            JOIN FETCH ph.product p
            JOIN FETCH ph.shopBranch sb
            JOIN FETCH sb.shop
            WHERE  p.id = :productId AND sb.id = :shopBranchId
            ORDER BY ph.changeDate
            """;

    public PriceHistoryRepositoryImpl() {
        super(PriceHistory.class);
    }

    @Override
    public List<PriceHistory> findAllByProductIdAndShopBranchId(Long productId, Long shopBranchId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID, PriceHistory.class)
                .setParameter("productId", productId)
                .setParameter("shopBranchId", shopBranchId)
                .getResultList();
    }
}
