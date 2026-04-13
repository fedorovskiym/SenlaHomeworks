package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ProductCategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractGenericRepositoryImpl<ProductCategory, Long> implements ProductCategoryRepository {

    private static final String HQL_FIND_BY_NAME = """
            SELECT pc FROM ProductCategory pc WHERE pc.name = :name
            """;
    private static final String HQL_FIND_ALL_PRODUCT_CATEGORY_ORDER_BY = """
                    SELECT pc FROM ProductCategory pc ORDER BY pc.id
            """;

    public ProductCategoryRepositoryImpl() {
        super(ProductCategory.class);
    }

    @Override
    public Optional<ProductCategory> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.createQuery(HQL_FIND_BY_NAME, ProductCategory.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<ProductCategory> findWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_PRODUCT_CATEGORY_ORDER_BY, ProductCategory.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }
}
