package com.senla.ProductService.repository;

import com.senla.ProductService.model.ProductCategory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductCategoryRepository extends GenericRepository<ProductCategory, UUID> {

    Optional<ProductCategory> findByName(String name);

    Map<UUID, ProductCategory> findAllById(Set<UUID> setProductCategoryId);
}
