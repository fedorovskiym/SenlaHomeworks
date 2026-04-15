package com.senla.ProductService.repository;

import com.senla.ProductService.model.ProductPrice;

import java.util.List;

public interface ProductPriceRepository extends GenericRepository<ProductPrice, Long> {

    List<ProductPrice> findAllWithPagination(Integer page, Integer size);
}
