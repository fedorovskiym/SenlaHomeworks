package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;

import java.util.List;

public interface CityService {

    CityDTO saveCity(CityDTO cityDTO);

    List<CityDTO> findAll();

    CityDTO getCityById(Long id);

    City getCityByNameIfExists(String name);

    City getCityByIdIfExists(Long id);

    CityDTO update(Long id, CityDTO cityDTO);
}
