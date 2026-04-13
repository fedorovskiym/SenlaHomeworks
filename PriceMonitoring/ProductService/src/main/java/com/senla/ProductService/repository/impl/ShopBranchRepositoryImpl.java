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
            WHERE sb.city.id = :cityId
            ORDER BY sb.id
            """;

    public ShopBranchRepositoryImpl() {
        super(ShopBranch.class);
    }

    @Override
    public List<ShopBranch> findAllByCityId(Long cityId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_BY_CITY_ORDER_BY_ID, ShopBranch.class)
                .setParameter("cityId", cityId)
                .getResultList();
    }
}
