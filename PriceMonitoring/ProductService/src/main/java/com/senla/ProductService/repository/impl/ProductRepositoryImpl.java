package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Product;
import com.senla.ProductService.repository.ProductRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl extends AbstractGenericRepositoryImpl<Product, Long> implements ProductRepository {

    public ProductRepositoryImpl() {
        super(Product.class);
    }
}
