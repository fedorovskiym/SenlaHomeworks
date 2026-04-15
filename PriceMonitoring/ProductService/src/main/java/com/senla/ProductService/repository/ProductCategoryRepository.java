package com.senla.ProductService.repository;

import com.senla.ProductService.model.ProductCategory;

import java.util.Optional;

public interface ProductCategoryRepository extends GenericRepository<ProductCategory, Long> {

    Optional<ProductCategory> findByName(String name);
}
