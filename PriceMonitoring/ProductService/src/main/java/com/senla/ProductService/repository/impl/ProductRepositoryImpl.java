package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ProductRepositoryImpl extends AbstractGenericRepositoryImpl<Product, UUID> implements ProductRepository {

    private static final String HQL_FIND_ALL_WITH_FETCH = """
            SELECT p FROM Product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            """;

    private static final String HQL_FIND_PRODUCT_PRICES_WITH_FETCH = """
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.priceList pl
            LEFT JOIN FETCH p.productCategory
            LEFT JOIN FETCH p.brand
            LEFT JOIN FETCH pl.shopBranch sb
            LEFT JOIN FETCH sb.shop
            LEFT JOIN FETCH sb.city
            WHERE sb.id = :id
            """;

    private static final String HQL_FIND_BY_NAME = """
            SELECT p FROM Product p
            WHERE p.name = :name
            """;

    public ProductRepositoryImpl() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        EntityManager entityManager = getEntityManager();
        return entityManager.createQuery(HQL_FIND_ALL_WITH_FETCH, Product.class).getResultList();
    }

    @Override
    public Optional<Product> findByIdWithPrices(UUID id) {
        EntityManager entityManager = getEntityManager();

        return Optional.ofNullable(entityManager.createQuery(HQL_FIND_PRODUCT_PRICES_WITH_FETCH, Product.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    @Override
    public void saveList(List<Product> saveList) {
        EntityManager entityManager = getEntityManager();
        saveList.forEach(entityManager::persist);
        entityManager.flush();
    }

    @Override
    public Optional<Product> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_NAME, Product.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void updateList(List<Product> updateList) {
        EntityManager entityManager = getEntityManager();
        updateList.forEach(entityManager::merge);
    }

}
