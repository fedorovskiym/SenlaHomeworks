package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.repository.ProductPriceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductPriceRepositoryImpl extends AbstractGenericRepositoryImpl<ProductPrice, Long> implements ProductPriceRepository {

    private static final String HQL_FIND_ALL_ORDER_BY = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop s
            JOIN FETCH pb.city c
            ORDER BY pp.id
            """;

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }

    @Override
    public List<ProductPrice> findAllWithPagination(Integer page, Integer size) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_ALL_ORDER_BY, ProductPrice.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .getResultList();
    }
}
