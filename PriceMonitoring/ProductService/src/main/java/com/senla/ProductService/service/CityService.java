package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;

import java.util.List;

public interface CityService {

    void saveCity(CityDTO cityDTO);

    List<CityDTO> findAllWithPagination(Integer page, Integer size);

    CityDTO getCityById(Long id);
}
