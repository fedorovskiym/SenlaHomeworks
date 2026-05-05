package com.senla.ProductService.repository;

import com.senla.ProductService.model.Brand;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface BrandRepository extends GenericRepository<Brand, UUID> {

    Optional<Brand> findByName(String name);
}
