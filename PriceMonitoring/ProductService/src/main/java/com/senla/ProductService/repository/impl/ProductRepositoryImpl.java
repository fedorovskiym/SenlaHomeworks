package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductRepositoryImpl extends AbstractGenericRepositoryImpl<Product, Long> implements ProductRepository {

    private static final String HQL_FIND_ALL_WITH_FETCH = """
            SELECT p FROM Product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            """;

    public ProductRepositoryImpl() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        EntityManager entityManager = getEntityManager();
        return entityManager.createQuery(HQL_FIND_ALL_WITH_FETCH, Product.class).getResultList();
    }
}
