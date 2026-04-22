package com.senla.ProductService.repository;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.model.ProductPrice;

import java.util.List;
import java.util.Optional;

public interface ProductPriceRepository extends GenericRepository<ProductPrice, Long> {

    List<ProductPrice> findAllWithPagination(Integer page, Integer size, Long shopBranchId, String sortBy, Boolean asc, Long brandId, Long categoryId);

    List<ProductPrice> findProductInShops(Long productId, Long cityId);

    Optional<ProductPrice> findByProductIdAndShopBranchId(Long productId, Long shopBranchId);

    void saveList(List<ProductPrice> saveList);

    void updateList(List<ProductPrice> updateList);

    List<ProductPrice> findByUserQuery(Long cityId, String productName, String categoryName, String brandName, String description);
}
