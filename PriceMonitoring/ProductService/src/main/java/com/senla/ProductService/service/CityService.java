package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;

import java.util.List;
import java.util.UUID;

public interface CityService {

    CityDTO saveCity(CityDTO cityDTO);

    List<CityDTO> findAllWithPagination(Integer page, Integer size);

    CityDTO getCityById(UUID id);

    City getCityByNameIfExists(String name);

    City getCityByIdIfExists(UUID id);

    CityDTO update(UUID id, CityDTO cityDTO);
}
