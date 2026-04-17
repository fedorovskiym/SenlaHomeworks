package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl extends AbstractGenericRepositoryImpl<Product, Long> implements ProductRepository {

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

    public ProductRepositoryImpl() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        EntityManager entityManager = getEntityManager();
        return entityManager.createQuery(HQL_FIND_ALL_WITH_FETCH, Product.class).getResultList();
    }

    @Override
    public Optional<Product> findByIdWithPrices(Long id) {
        EntityManager entityManager = getEntityManager();

        return Optional.ofNullable(entityManager.createQuery(HQL_FIND_PRODUCT_PRICES_WITH_FETCH, Product.class)
                .setParameter("id", id)
                .getSingleResult());
    }
}
