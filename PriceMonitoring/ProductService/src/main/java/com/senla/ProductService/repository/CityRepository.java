package com.senla.ProductService.repository;

import com.senla.ProductService.model.City;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CityRepository extends GenericRepository<City, UUID>{

    Optional<City> findByName(String name);
}
