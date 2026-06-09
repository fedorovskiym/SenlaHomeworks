package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
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
public class BrandRepositoryImpl extends AbstractGenericRepositoryImpl<Brand, UUID> implements BrandRepository {
    private static final String HQL_FIND_BY_NAME = """
            SELECT b FROM Brand b WHERE b.name = :name
            """;

    private static final String HQL_FIND_ALL_BY_ID = """
            SELECT b FROM Brand b
            WHERE b.id IN (:setBrandId)
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

    @Override
    public Map<UUID, Brand> findAllById(Set<UUID> setBrandId) {
        EntityManager entityManager = getEntityManager();

        List<Brand> brands = entityManager.createQuery(HQL_FIND_ALL_BY_ID, Brand.class)
                .setParameter("setBrandId", setBrandId)
                .getResultList();

        return brands.stream().collect(Collectors.toMap(Brand::getId, Function.identity()));
    }
}
