package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.repository.ProductPriceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductPriceRepositoryImpl extends AbstractGenericRepositoryImpl<ProductPrice, Long> implements ProductPriceRepository {

    private static final String HQL_FIND_ALL = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop s
            JOIN FETCH pb.city c
            WHERE pp.shopBranch.id = :shopBranchId AND pp.status = 'ACTUAL'
            """;

    private static final String HQL_FIND_PRODUCT_IN_SHOPS = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop s
            JOIN FETCH pb.city c
            WHERE p.id = :productId AND c.id = :cityId AND pp.status = 'ACTUAL'
            ORDER BY pp.price ASC
            """;

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }

    @Override
    public List<ProductPrice> findAllWithPagination(Integer page, Integer size, Long shopBranchId, String sortBy, Boolean asc, Long brandId, Long categoryId) {
        EntityManager entityManager = getEntityManager();
        String hql = HQL_FIND_ALL;

        if(brandId != null) {
            hql = hql.concat("AND p.brand.id = ".concat(String.valueOf(brandId)).concat(" "));
        }
        if(categoryId != null) {
            hql = hql.concat("AND p.productCategory.id = ".concat(String.valueOf(categoryId)).concat(" "));
        }
        hql = hql.concat("ORDER BY pp.".concat(sortBy).concat(" ").concat(asc ? "ASC" : "DESC"));

        return entityManager.createQuery(hql, ProductPrice.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size)
                .setParameter("shopBranchId", shopBranchId)
                .getResultList();
    }

    @Override
    public List<ProductPrice> findProductInShops(Long productId, Long cityId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_PRODUCT_IN_SHOPS, ProductPrice.class)
                .setParameter("productId", productId)
                .setParameter("cityId", cityId)
                .getResultList();
    }
}
