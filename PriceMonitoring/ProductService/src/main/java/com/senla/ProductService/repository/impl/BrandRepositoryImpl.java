package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Brand;
import com.senla.ProductService.repository.BrandRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BrandRepositoryImpl extends AbstractGenericRepositoryImpl<Brand, Long> implements BrandRepository {

    private static final String HQL_FIND_ALL_ORDER_BY_ID = """
            SELECT b FROM Brand b ORDER BY b.id
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
}
