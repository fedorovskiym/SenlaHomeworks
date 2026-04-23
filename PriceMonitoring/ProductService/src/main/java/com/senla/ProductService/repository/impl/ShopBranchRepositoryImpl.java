package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ShopBranchRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ShopBranchRepositoryImpl extends AbstractGenericRepositoryImpl<ShopBranch, Long> implements ShopBranchRepository {

    private static final String HQL_FIND_ALL_BY_CITY_ORDER_BY_ID = """
            SELECT sb FROM ShopBranch sb
            JOIN FETCH sb.city
            JOIN FETCH sb.shop s
            WHERE s.id = :shopId
            ORDER BY sb.id
            """;

    public ShopBranchRepositoryImpl() {
        super(ShopBranch.class);
    }

    @Override
    public List<ShopBranch> findAllByShopIdFetch(Long shopId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_BY_CITY_ORDER_BY_ID, ShopBranch.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }
}
