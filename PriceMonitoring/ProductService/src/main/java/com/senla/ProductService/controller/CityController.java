package com.senla.ProductService.controller;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.service.CityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/city")
@Validated
public class CityController {

    private final CityService cityService;
    private static final Logger logger = LoggerFactory.getLogger(CityController.class);

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<CityDTO>> findAll(
            @Min(1) @RequestParam Integer page,
            @Min(1) @RequestParam Integer size) {
        logger.info("Recieved request to get all cities /api/product-service/city/");
        return ResponseEntity.status(HttpStatus.OK).body(cityService.findAllWithPagination(page, size));
    }

    @PostMapping(value = "/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CityDTO> createCity(@Valid @RequestBody CityDTO cityDTO) {
        logger.info("Recieved request to save a city /api/product-service/city/");
        CityDTO createdCity = cityService.saveCity(cityDTO);
        logger.info("Succesfull save a city /api/product-service/city/");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCity);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CityDTO> getCityById(@PathVariable UUID id) {
        logger.info("Recieved request to get city by id /api/product-service/city/id/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(cityService.getCityById(id));
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<CityDTO> updateCity(
            @PathVariable UUID id,
            @Valid @RequestBody CityDTO cityDTO) {
        logger.info("Recieved request to update city by id /api/product-service/city/id/{}", id);
        CityDTO updatedCity = cityService.update(id, cityDTO);
        logger.info("Succesfull update city by id /api/product-service/city/id/{}", id);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCity);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deleteCityById(@PathVariable UUID id) {
        logger.info("Recieved request to delete city by id /api/product-service/city/id/{}", id);
        cityService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(HttpStatus.OK);
    }
}