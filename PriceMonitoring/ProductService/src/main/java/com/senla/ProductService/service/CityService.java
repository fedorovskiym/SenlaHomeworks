package com.senla.ProductService.service;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.model.City;
import jakarta.persistence.EntityExistsException;

import java.util.List;
import java.util.UUID;

/**
 * interface for work with cities
 */
public interface CityService {

    /**
     * method for saving city
     *
     * @param cityDTO contains city data
     * @return cityDTO mapped from saved city
     * @throws EntityExistsException if city with name from cityDTO already exists
     */
    CityDTO saveCity(CityDTO cityDTO);

    /**
     * method for finding all cities with pagination
     *
     * @param page number of page
     * @param size page size
     * @return list cityDTO mapped from list city
     */
    List<CityDTO> findAllWithPagination(Integer page, Integer size);

    /**
     * method for find cityDTO by id
     *
     * @param id city id from request
     * @return cityDTO mapped from city
     */
    CityDTO getCityById(UUID id);

    /**
     * method for find city by name
     *
     * @param name city name from request
     * @return city with name from request
     * @throws EntityExistsException if city with name from request already exists
     */
    City getCityByNameIfExists(String name);

    /**
     * method for find city by id
     *
     * @param id city id from request
     * @return city with id from request
     * @throws EntityExistsException if city with id from request already exists
     */
    City getCityByIdIfExists(UUID id);

    /**
     * method for update city
     *
     * @param id city id from request to update
     * @param cityDTO city data (name)
     * @return cityDTO from updated city
     * @throws EntityExistsException if city with new name from cityDTO exists or name is null
     */
    CityDTO update(UUID id, CityDTO cityDTO);
}
