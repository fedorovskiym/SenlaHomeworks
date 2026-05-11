package com.senla.ProductService.repository;

import com.senla.ProductService.dto.product.ProductSearchDTO;
import com.senla.ProductService.model.Product;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface ProductRepository extends GenericRepository<Product, UUID> {

    Optional<Product> findByIdWithPrices(UUID id);

    void saveList(List<Product> saveList);

    Optional<Product> findByName(String name);

    void updateList(List<Product> updateList);

    Map<UUID, Product> findAllById(Set<UUID> listProductId);

    List<Product> findAllWithPagination(ProductSearchDTO productSearchDTO);
}
