package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ProductCategoryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractGenericRepositoryImpl<ProductCategory, UUID> implements ProductCategoryRepository {

    private static final String HQL_FIND_BY_NAME = """
            SELECT pc FROM ProductCategory pc WHERE pc.name = :name
            """;

    private static final String HQL_FIND_ALL_BY_ID = """
            SELECT pc FROM ProductCategory pc
            WHERE pc.id IN (:setProductCategoryId)
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
    public Map<UUID, ProductCategory> findAllById(Set<UUID> setProductCategoryId) {
        EntityManager entityManager = getEntityManager();

        List<ProductCategory> productCategories = entityManager.createQuery(HQL_FIND_ALL_BY_ID, ProductCategory.class)
                .setParameter("setProductCategoryId", setProductCategoryId)
                .getResultList();

        System.out.println(productCategories.size());
        return productCategories.stream().collect(Collectors.toMap(ProductCategory::getId, Function.identity()));
    }
}
