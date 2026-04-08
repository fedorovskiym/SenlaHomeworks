package com.senla.ProductService.repository.impl;

import com.senla.ProductService.model.Shop;
import com.senla.ProductService.repository.ShopRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ShopRepositoryImpl extends AbstractGenericRepositoryImpl<Shop, Long> implements ShopRepository {

    public ShopRepositoryImpl() {
        super(Shop.class);
    }
}
