package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.mapper.CityMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.repository.CityRepository;
import com.senla.ProductService.service.CityService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;
    private static final Logger logger = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    public CityServiceImpl(CityRepository cityRepository, CityMapper cityMapper) {
        this.cityRepository = cityRepository;
        this.cityMapper = cityMapper;
    }

    @Override
    @Transactional
    public CityDTO saveCity(CityDTO cityDTO) {
        logger.info("Saving city from dto {}", cityDTO);
        if (getCityByNameIfExists(cityDTO.name()) != null) {
            logger.warn("City with name {} already exists", cityDTO.name());
            throw new EntityExistsException("City with name " + cityDTO.name() + " already exists");
        }
        City city = cityMapper.cityDTOToCity(cityDTO);
        cityRepository.save(city);
        logger.info("Succesfully saved city {}", city);
        return cityMapper.cityToCityDTO(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDTO> findAll() {//пагианция
        logger.info("Finding all cities");
        return cityRepository.findAll().stream().map(cityMapper::cityToCityDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CityDTO getCityById(Long id) {
        logger.info("Finding city with id {}", id);
        return cityMapper.cityToCityDTO(getCityByIdIfExists(id));
    }

    @Override
    @Transactional(readOnly = true)
    public City getCityByNameIfExists(String name) {
        logger.info("Finding city with name {}", name);
        return cityRepository.findByName(name).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public City getCityByIdIfExists(Long id) {
        logger.info("Finding city with id if exists {}", id);
        return cityRepository.findById(id).orElseThrow(() -> {
            logger.warn("City with id {} not found", id);
            return new EntityNotFoundException("City with id - " + id + " not found!");
        });
    }

    @Override
    @Transactional
    public CityDTO update(Long id, CityDTO cityDTO) {
        logger.info("Updating city with id {}", id);
        City city = getCityByIdIfExists(id);

        if (city.getName().equals(cityDTO.name()) || getCityByNameIfExists(cityDTO.name()) != null) {
            logger.warn("City with name {} already exists", cityDTO.name());
            throw new EntityExistsException("City with name - " + cityDTO.name() + " already exists!");
        }

        city.setName(cityDTO.name());
        cityRepository.update(city);
        logger.info("Succesfully updated city {}", city);
        return cityMapper.cityToCityDTO(city);
    }

}
