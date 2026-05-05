package com.senla.ProductService.repository;

import com.senla.ProductService.model.ProductCategory;

import java.util.Optional;
import java.util.UUID;

public interface ProductCategoryRepository extends GenericRepository<ProductCategory, UUID> {

    Optional<ProductCategory> findByName(String name);
}
