package com.senla.ProductService.repository;

import com.senla.ProductService.model.Brand;

import java.util.List;
import java.util.Optional;


public interface BrandRepository extends GenericRepository<Brand, Long> {

    List<Brand> findWithPagination(Integer page, Integer size);

    Optional<Brand> findByName(String name);
}
