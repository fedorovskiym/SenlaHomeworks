package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.mapper.CityMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.repository.CityRepository;
import com.senla.ProductService.service.CityService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    @Autowired
    public CityServiceImpl(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    @Override
    @Transactional
    public void saveCity(CityDTO cityDTO) {
        if(cityRepository.findByName(cityDTO.name()).isPresent()) {
            throw new EntityExistsException("City with name - " + cityDTO.name() + " already exists!");
        }
        City city = cityMapper.cityDTOToCity(cityDTO);
        cityRepository.save(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> findAllWithPagination(Integer page, Integer size) {
        return cityRepository.findWithPagination(page, size).stream().map(cityMapper::cityToCityDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        City city = cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("City with id - " + id + " not found!"));
        return cityMapper.cityToCityDTO(city);
    }
}
