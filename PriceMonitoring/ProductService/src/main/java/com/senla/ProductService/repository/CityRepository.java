package com.senla.ProductService.repository;

import com.senla.ProductService.model.City;

import java.util.List;
import java.util.Optional;

public interface CityRepository extends GenericRepository<City, Long>{

    List<City> findWithPagination(Integer page, Integer size);

    Optional<City> findByName(String name);
}
