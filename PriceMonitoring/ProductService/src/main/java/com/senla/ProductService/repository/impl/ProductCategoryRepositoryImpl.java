package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductCategory;
import com.senla.ProductService.repository.ProductCategoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractGenericRepositoryImpl<ProductCategory, Long> implements ProductCategoryRepository {

    public ProductCategoryRepositoryImpl() {
        super(ProductCategory.class);
    }
}
