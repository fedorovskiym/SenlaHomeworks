package com.senla.ProductService.repository.impl;

import com.senla.ProductService.dto.price.ProductPriceSearchDTO;
import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.model.enums.PriceStatus;
import com.senla.ProductService.repository.ProductPriceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ProductPriceRepositoryImpl extends AbstractGenericRepositoryImpl<ProductPrice, UUID> implements ProductPriceRepository {

    private static final String HQL_FIND_ALL = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop s
            JOIN FETCH pb.city c
            WHERE pp.shopBranch.id = :shopBranchId
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

    private static final String HQL_FIND_BY_ID_WITH_FETCH = """
            SELECT pp FROM ProductPrice pp
            JOIN FETCH pp.product p
            JOIN FETCH pp.shopBranch pb
            JOIN FETCH pb.shop
            WHERE pp.id = :productPriceId
            """;

    private static final String HQL_FIND_BY_ID = """
            SELECT pp FROM ProductPrice pp
            WHERE pp.id IN (:listProductPriceId)
            """;

    private static final String HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID_AND_STATUS = """
            SELECT pp FROM ProductPrice pp
            WHERE pp.product.id = :productId AND pp.shopBranch.id = :shopBranchId AND pp.status = 'ACTUAL'
            """;

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }

    @Override
    public List<ProductPrice> findAllWithPagination(ProductPriceSearchDTO productPriceSearchDTO) {
        EntityManager entityManager = getEntityManager();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HQL_FIND_ALL);

        if (productPriceSearchDTO.brandId() != null) {
            stringBuilder.append("AND p.brand.id = :brandId\n");
        }
        if (productPriceSearchDTO.categoryId() != null) {
            stringBuilder.append("AND p.category.id = :categoryId\n");
        }
        if (productPriceSearchDTO.status() != null) {
            stringBuilder.append("AND pp.status = :status\n");
        }
        stringBuilder.append("ORDER BY pp.").append(productPriceSearchDTO.sortBy()).append(" ");
        stringBuilder.append((productPriceSearchDTO.asc() ? "ASC" : "DESC"));

        TypedQuery<ProductPrice> query = entityManager.createQuery(stringBuilder.toString(), ProductPrice.class);
        if (productPriceSearchDTO.brandId() != null) {
            query.setParameter("brandId", productPriceSearchDTO.brandId());
        }
        if (productPriceSearchDTO.categoryId() != null) {
            query.setParameter("categoryId", productPriceSearchDTO.categoryId());
        }
        if (productPriceSearchDTO.status() != null) {
            query.setParameter("status", productPriceSearchDTO.status());
        }

        return query
                .setFirstResult((productPriceSearchDTO.page() - 1) * productPriceSearchDTO.size())
                .setMaxResults(productPriceSearchDTO.size())
                .setParameter("shopBranchId", productPriceSearchDTO.shopBranchId())
                .getResultList();
    }

    @Override
    public List<ProductPrice> findProductInShops(UUID productId, UUID cityId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_PRODUCT_IN_SHOPS, ProductPrice.class)
                .setParameter("productId", productId)
                .setParameter("cityId", cityId)
                .getResultList();
    }

    @Override
    public Optional<ProductPrice> findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId) {
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
    public List<ProductPrice> findByUserQuery(UUID cityId, String productName, String categoryName,
                                              String brandName, String description) {
        EntityManager entityManager = getEntityManager();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(HQL_FIND_PRODUCT_PRICES_BY_USER_REQUEST);

        if (productName != null) {
            stringBuilder.append("AND LOWER(p.name) LIKE '%").append(productName.toLowerCase()).append("%'");
        }
        if (categoryName != null) {
            stringBuilder.append(" AND LOWER(pc.name) LIKE '%").append(categoryName.toLowerCase()).append("%'");
        }
        if (brandName != null) {
            stringBuilder.append(" AND LOWER(b.name) LIKE '%").append(brandName.toLowerCase()).append("%'");
        }
        if (description != null) {
            stringBuilder.append(" AND LOWER(p.description) LIKE '%").append(description.toLowerCase()).append("%'");
        }

        return entityManager.createQuery(stringBuilder.toString(), ProductPrice.class)
                .setParameter("cityId", cityId)
                .getResultList();
    }

    @Override
    public Optional<ProductPrice> findByIdWithFetch(UUID id) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_ID_WITH_FETCH, ProductPrice.class)
                .setParameter("productPriceId", id)
                .getResultStream()
                .findFirst();
    }

    @Override
    public Map<UUID, ProductPrice> findAllById(Set<UUID> listProductPriceId) {
        EntityManager entityManager = getEntityManager();

        List<ProductPrice> productPriceList = entityManager.createQuery(HQL_FIND_BY_ID, ProductPrice.class)
                .setParameter("listProductPriceId", listProductPriceId)
                .getResultList();

        return productPriceList.stream().collect(Collectors.toMap(ProductPrice::getId, Function.identity()));
    }

    @Override
    public ProductPrice findByProductIdAndShopBranchIdAndStatus(UUID productId, UUID shopBranchId) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_PRODUCT_ID_AND_SHOP_BRANCH_ID_AND_STATUS, ProductPrice.class)
                .setParameter("productId", productId)
                .setParameter("shopBranchId", shopBranchId)
                .getSingleResult();
    }
}
