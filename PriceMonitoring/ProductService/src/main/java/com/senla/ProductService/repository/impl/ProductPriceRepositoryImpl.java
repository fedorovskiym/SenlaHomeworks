package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.repository.ProductPriceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Table;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    private static final String HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH pp.shopBranch sb
            WHERE p.id = :productId AND sb.id = :shopBranchId AND pp.status = 'ACTUAL'
            """;

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }

    @Override
    public List<ProductPrice> findAllWithPagination(Integer page, Integer size, Long shopBranchId, String sortBy, Boolean asc, Long brandId, Long categoryId) {
        EntityManager entityManager = getEntityManager();
        String hql = HQL_FIND_ALL;

        if (brandId != null) {
            hql = hql.concat("AND p.brand.id = ".concat(String.valueOf(brandId)).concat(" "));
        }
        if (categoryId != null) {
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

    @Override
    public Optional<ProductPrice> findByProductIdAndShopBranchId(Long productId, Long shopBranchId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID, ProductPrice.class)
                .setParameter("productId", productId)
                .setParameter("shopBranchId", shopBranchId)
                .getResultStream()
                .findFirst();
    }

    @Override
    @Transactional
    public void saveList(List<ProductPrice> saveList) {
        EntityManager entityManager = getEntityManager();

        saveList.forEach(entityManager::persist);

        entityManager.flush();
    }

    @Override
    @Transactional
    public void updateList(List<ProductPrice> updateList) {
        EntityManager entityManager = getEntityManager();

        updateList.forEach(entityManager::merge);

        entityManager.flush();
    }
}
