package com.senla.ProductService.repository;

import com.senla.ProductService.model.Shop;

import java.util.List;
import java.util.Optional;


public interface ShopRepository extends GenericRepository<Shop, Long> {

    Optional<Shop> findByName(String name);

    List<Shop> findAllWithPagination(Integer page, Integer size);
}
