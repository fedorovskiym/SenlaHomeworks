package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ShopRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ShopRepositoryImpl extends AbstractGenericRepositoryImpl<Shop, Long> implements ShopRepository {

    private static final String HQL_FIND_BY_NAME = """
            SELECT s FROM Shop s WHERE s.name = :name
            """;

    private static final String HQL_FIND_ALL_ORDER_BY_ID = """
            SELECT s FROM Shop s ORDER BY s.id
            """;

    public ShopRepositoryImpl() {
        super(Shop.class);
    }

    @Override
    public Optional<Shop> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.createQuery(HQL_FIND_BY_NAME, Shop.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Shop> findAllWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_ORDER_BY_ID, Shop.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }
}
