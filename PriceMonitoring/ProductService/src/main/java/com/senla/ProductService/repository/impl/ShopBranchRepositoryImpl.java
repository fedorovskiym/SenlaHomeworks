package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ShopBranch;
import com.senla.ProductService.repository.ShopBranchRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ShopBranchRepositoryImpl extends AbstractGenericRepositoryImpl<ShopBranch, UUID> implements ShopBranchRepository {

    private static final String HQL_FIND_ALL_BY_CITY_ORDER_BY_ID = """
            SELECT sb FROM ShopBranch sb
            JOIN FETCH sb.city
            JOIN FETCH sb.shop s
            WHERE s.id = :shopId
            ORDER BY sb.id
            """;

    private static final String HQL_FIND_ALL_BY_ID = """
            SELECT sb FROM ShopBranch sb
            WHERE sb.id IN (:listShopBranchId)
            """;

    public ShopBranchRepositoryImpl() {
        super(ShopBranch.class);
    }

    @Override
    public List<ShopBranch> findAllByShopIdFetch(UUID shopId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_BY_CITY_ORDER_BY_ID, ShopBranch.class)
                .setParameter("shopId", shopId)
                .getResultList();
    }

    @Override
    public Map<UUID, ShopBranch> findAllById(Set<UUID> listShopBranchId) {
        EntityManager entityManager = getEntityManager();

        List<ShopBranch> shopBranches = entityManager.createQuery(HQL_FIND_ALL_BY_ID, ShopBranch.class)
                .setParameter("listShopBranchId", listShopBranchId)
                .getResultList();

        return shopBranches.stream().collect(Collectors.toMap(ShopBranch::getId, Function.identity()));
    }
}
