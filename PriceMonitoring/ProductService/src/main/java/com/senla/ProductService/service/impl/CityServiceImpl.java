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
        if (getCityByNameIfExists(cityDTO.name()) != null) {
            throw new EntityExistsException("City with name " + cityDTO.name() + " already exists");
        }
        City city = cityMapper.cityDTOToCity(cityDTO);
        cityRepository.save(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {
        return cityRepository.findAll().stream().map(cityMapper::cityToCityDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        return cityMapper.cityToCityDTO(getCityByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public City getCityByNameIfExists(String name) {
        return cityRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public City getCityByIdIfExists(Long id) {
        return cityRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("City with id - " + id + " not found!")
        );
    }

    @Override
    @Transactional
    public void update(Long id, CityDTO cityDTO) {
        City city = getCityByIdIfExists(id);

        if (city.getName().equals(cityDTO.name()) || getCityByNameIfExists(cityDTO.name()) != null) {
            throw new EntityExistsException("City with name - " + cityDTO.name() + " already exists!");
        }

        city.setName(cityDTO.name());
        cityRepository.update(city);
    }

}
