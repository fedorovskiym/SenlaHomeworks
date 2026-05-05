package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class BrandRepositoryImpl extends AbstractGenericRepositoryImpl<Brand, UUID> implements BrandRepository {
    private static final String HQL_FIND_BY_NAME = """
            SELECT b FROM Brand b WHERE b.name = :name
            """;

    public BrandRepositoryImpl() {
        super(Brand.class);
    }

    @Override
    public Optional<Brand> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        try {
            return Optional.ofNullable(entityManager.createQuery(HQL_FIND_BY_NAME, Brand.class)
                    .setParameter("name", name)
                    .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
