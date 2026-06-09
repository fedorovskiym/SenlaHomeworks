package com.senla.ProductService.repository;

import com.senla.ProductService.model.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ShopRepository extends GenericRepository<Shop, UUID> {

    Optional<Shop> findByName(String name);

    List<Shop> findAllByCityId(UUID cityId);
}
