package com.senla.ProductService.repository;

import com.senla.ProductService.dto.price.ProductPriceDTO;
import com.senla.ProductService.model.ProductPrice;

import java.util.List;

public interface ProductPriceRepository extends GenericRepository<ProductPrice, Long> {

    List<ProductPrice> findAllWithPagination(Integer page, Integer size, Long shopBranchId, String sortBy, Boolean asc, Long brandId, Long categoryId);

    List<ProductPrice> findProductInShops(Long productId, Long cityId);
}
