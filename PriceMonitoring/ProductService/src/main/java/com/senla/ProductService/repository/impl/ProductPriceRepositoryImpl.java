package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Product;
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

    private static final String HQL_FIND_PRODUCT_PRICES_BY_USER_REQUEST = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop s
            JOIN FETCH pb.city c
            WHERE c.id = :cityId AND pp.status = 'ACTUAL'
            """;

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }

    @Override
    public List<ProductPrice> findAllWithPagination(Integer page, Integer size, Long shopBranchId, String sortBy, Boolean asc, Long brandId, Long categoryId) {
        EntityManager entityManager = getEntityManager();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HQL_FIND_ALL);

        if (brandId != null) {
            stringBuilder.append("AND pp.brand.id = ").append(brandId);
        }
        if (categoryId != null) {
            stringBuilder.append("AND pp.category.id = ").append(categoryId);
        }
        stringBuilder.append("ORDER BY pp.price ").append((asc ? "ASC" : "DESC"));;

        return entityManager.createQuery(stringBuilder.toString(), ProductPrice.class)
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
    public void saveList(List<ProductPrice> saveList) {
        EntityManager entityManager = getEntityManager();

        saveList.forEach(entityManager::persist);

        entityManager.flush();
    }

    @Override
    public void updateList(List<ProductPrice> updateList) {
        EntityManager entityManager = getEntityManager();

        updateList.forEach(entityManager::merge);

        entityManager.flush();
    }


    @Override
    public List<ProductPrice> findByUserQuery(Long cityId, String productName, String categoryName, String brandName, String description) {
        EntityManager entityManager = getEntityManager();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HQL_FIND_PRODUCT_PRICES_BY_USER_REQUEST);

        if (productName != null) {
            stringBuilder.append("AND LOWER(p.name) LIKE '%").append(productName.toLowerCase()).append("%'");
        }
        if (categoryName != null) {
            stringBuilder.append("AND LOWER(pc.name) LIKE '%").append(categoryName.toLowerCase()).append("%'");
        }
        if (brandName != null) {
            stringBuilder.append("AND LOWER(b.name) LIKE '%").append(brandName.toLowerCase()).append("%'");
        }
        if (description != null) {
            stringBuilder.append("AND LOWER(p.description) LIKE '%").append(description.toLowerCase()).append("%'");
        }

        return entityManager.createQuery(stringBuilder.toString(), ProductPrice.class)
                .setParameter("cityId", cityId)
                .getResultList();
    }
}
