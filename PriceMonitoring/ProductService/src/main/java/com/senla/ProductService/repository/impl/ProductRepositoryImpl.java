package com.senla.ProductService.repository.impl;

import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.model.Product;
import com.senla.ProductService.repository.ProductRepository;
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
public class ProductRepositoryImpl extends AbstractGenericRepositoryImpl<Product, UUID> implements ProductRepository {

    private static final String HQL_FIND_ALL_WITH_FETCH = """
            SELECT p FROM Product p
            JOIN FETCH p.brand b
            JOIN FETCH p.productCategory pc
            WHERE 1 = 1
            """;

    private static final String HQL_FIND_PRODUCT_PRICES_WITH_FETCH = """
            SELECT DISTINCT p FROM Product p
            LEFT JOIN FETCH p.priceList pl
            LEFT JOIN FETCH p.productCategory
            LEFT JOIN FETCH p.brand
            LEFT JOIN FETCH pl.shopBranch sb
            LEFT JOIN FETCH sb.shop
            LEFT JOIN FETCH sb.city
            WHERE sb.id = :id
            """;

    private static final String HQL_FIND_BY_NAME = """
            SELECT p FROM Product p
            WHERE p.name = :name
            """;

    private static final String HQL_FIND_BY_ID = """
            SELECT p FROM Product p
            WHERE p.id IN (:listProductId)
            """;

    public ProductRepositoryImpl() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        EntityManager entityManager = getEntityManager();
        return entityManager.createQuery(HQL_FIND_ALL_WITH_FETCH, Product.class).getResultList();
    }

    @Override
    public Optional<Product> findByIdWithPrices(UUID id) {
        EntityManager entityManager = getEntityManager();

        return Optional.ofNullable(entityManager.createQuery(HQL_FIND_PRODUCT_PRICES_WITH_FETCH, Product.class)
                .setParameter("id", id)
                .getSingleResult());
    }

    @Override
    public void saveList(List<Product> saveList) {
        EntityManager entityManager = getEntityManager();
        saveList.forEach(entityManager::persist);
        entityManager.flush();
    }

    @Override
    public Optional<Product> findByName(String name) {
        EntityManager entityManager = getEntityManager();

        return entityManager.createQuery(HQL_FIND_BY_NAME, Product.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst();
    }

    @Override
    public void updateList(List<Product> updateList) {
        EntityManager entityManager = getEntityManager();
        updateList.forEach(entityManager::merge);
    }

    @Override
    public Map<UUID, Product> findAllById(Set<UUID> listProductId) {
        EntityManager entityManager = getEntityManager();

        List<Product> productList = entityManager.createQuery(HQL_FIND_BY_ID, Product.class)
                .setParameter("listProductId", listProductId)
                .getResultList();

        return productList.stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @Override
    public List<Product> findAllWithPagination(ProductSearchDTO productSearchDTO) {
        EntityManager entityManager = getEntityManager();
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(HQL_FIND_ALL_WITH_FETCH);

        if (productSearchDTO.brandId() != null) {
            stringBuilder.append("AND p.brand.id = :brandId\n");
        }
        if (productSearchDTO.productCategoryId() != null) {
            stringBuilder.append("AND p.productCategory.id = :productCategoryId\n");
        }
        if (productSearchDTO.unit() != null) {
            stringBuilder.append("AND p.unit = :unit\n");
        }
        stringBuilder.append("ORDER BY p.").append(productSearchDTO.sortBy()).append(" ");
        stringBuilder.append((productSearchDTO.asc() ? "ASC" : "DESC"));

        TypedQuery<Product> query = entityManager.createQuery(stringBuilder.toString(), Product.class);
        if (productSearchDTO.brandId() != null) {
            query.setParameter("brandId", productSearchDTO.brandId());
        }
        if (productSearchDTO.productCategoryId() != null) {
            query.setParameter("productCategoryId", productSearchDTO.productCategoryId());
        }
        if (productSearchDTO.unit() != null) {
            query.setParameter("unit", productSearchDTO.unit());
        }

        return query
                .setFirstResult((productSearchDTO.page() - 1) * productSearchDTO.size())
                .setMaxResults(productSearchDTO.size())
                .getResultList();
    }

}
