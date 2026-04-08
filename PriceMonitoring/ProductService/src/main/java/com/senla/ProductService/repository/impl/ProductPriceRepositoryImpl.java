package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.ProductPrice;
import com.senla.ProductService.repository.ProductPriceRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProductPriceRepositoryImpl extends AbstractGenericRepositoryImpl<ProductPrice, Long> implements ProductPriceRepository {

    public ProductPriceRepositoryImpl() {
        super(ProductPrice.class);
    }
}
