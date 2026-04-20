package com.senla.ProductService.repository;

import com.senla.ProductService.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends GenericRepository<Product, Long> {

    Optional<Product> findByIdWithPrices(Long id);

    void saveList(List<Product> saveList);

    Optional<Product> findByName(String name);

    void updateList(List<Product> updateList);
}
