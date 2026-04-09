package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.kafka.common.quota.ClientQuotaAlteration;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BrandRepositoryImpl extends AbstractGenericRepositoryImpl<Brand, Long> implements BrandRepository {

    private static final String HQL_FIND_ALL_ORDER_BY_ID = """
            SELECT b FROM Brand b ORDER BY b.id
            """;

    private static final String HQL_FIND_BY_NAME = """
            SELECT b FROM Brand b WHERE  b.name = :name
            """;

    public BrandRepositoryImpl() {
        super(Brand.class);
    }

    @Override
    public List<Brand> findWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_ORDER_BY_ID, Brand.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
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
