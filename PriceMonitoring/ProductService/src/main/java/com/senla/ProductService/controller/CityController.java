package com.senla.ProductService.controller;

import com.senla.ProductService.annotation.CheckId;
import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.service.CityService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.DialectOverride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/city")
@Validated
public class CityController {

    private final CityService cityService;

    @Autowired
    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<CityDTO>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.findAll());
    }

    @PostMapping(value = "/")
    public ResponseEntity<?> createCity(@Valid @RequestBody CityDTO cityDTO) {
        cityService.saveCity(cityDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<CityDTO> getCityById(@CheckId @PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(cityService.getCityById(id));
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<?> updateCity(@CheckId @PathVariable Long id, @Valid @RequestBody CityDTO cityDTO) {
        cityService.update(id, cityDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}