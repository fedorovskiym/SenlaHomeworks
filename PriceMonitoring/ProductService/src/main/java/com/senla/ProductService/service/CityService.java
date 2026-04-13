package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;

import java.util.List;

public interface CityService {

    void saveCity(CityDTO cityDTO);

    List<CityDTO> findAll();

    CityDTO getCityById(Long id);

    City getCityByIdIfExists(Long id);
}
