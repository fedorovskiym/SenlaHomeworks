package com.senla.ProductService.repository;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductPriceRepository extends GenericRepository<ProductPrice, UUID> {

    List<ProductPrice> findAllWithPagination(Integer page, Integer size, UUID shopBranchId, String sortBy, Boolean asc, UUID brandId, UUID categoryId);

    List<ProductPrice> findProductInShops(UUID productId, UUID cityId);

    Optional<ProductPrice> findByProductIdAndShopBranchId(UUID productId, UUID shopBranchId);

    void saveList(List<ProductPrice> saveList);

    void updateList(List<ProductPrice> updateList);

    List<ProductPrice> findByUserQuery(UUID cityId, String productName, String categoryName, String brandName, String description);

    Optional<ProductPrice> findByIdWithFetch(UUID id);
}
