package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

public interface CityService {

    void saveCity(CityDTO cityDTO);

    List<CityDTO> findAll();

    CityDTO getCityById(Long id);

    City getCityByNameIfExists(String name);

    City getCityByIdIfExists(Long id);

    void update(Long id, CityDTO cityDTO);
}
