package com.senla.ProductService.service.impl;

import com.senla.ProductService.dto.CityDTO;
import com.senla.ProductService.mapper.CityMapper;
import com.senla.ProductService.model.City;
import com.senla.ProductService.repository.CityRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CityServiceImplTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityServiceImpl cityService;

    private City city;
    private CityDTO cityDTO;

    private final UUID cityId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        city = new City();
        city.setId(cityId);
        city.setName("city");

        cityDTO = new CityDTO(cityId, city.getName());
    }

    @Test
    void saveCityShouldThrowEntityExistsException() {
        when(cityRepository.findByName(cityDTO.name())).thenReturn(Optional.of(city));

        assertThrows(EntityExistsException.class, () -> cityService.saveCity(cityDTO));
    }

    @Test
    void saveCityShouldSaveSuccessfully() {
        when(cityRepository.findByName(cityDTO.name())).thenReturn(Optional.empty());
        when(cityMapper.cityDTOToCity(cityDTO)).thenReturn(city);
        when(cityMapper.cityToCityDTO(city)).thenReturn(cityDTO);

        CityDTO result = cityService.saveCity(cityDTO);

        assertEquals(cityDTO, result);

        verify(cityRepository).save(city);
    }

    @Test
    void findAllWithPaginationShouldReturnList() {
        when(cityRepository.findAllWithPagination(0, 10)).thenReturn(List.of(city));

        when(cityMapper.cityToCityDTO(city)).thenReturn(cityDTO);

        List<CityDTO> result = cityService.findAllWithPagination(0, 10);

        assertEquals(1, result.size());
        assertEquals(cityDTO, result.get(0));
    }

    @Test
    void getCityByIdShouldReturnDTO() {
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(cityMapper.cityToCityDTO(city)).thenReturn(cityDTO);

        CityDTO result = cityService.getCityById(cityId);

        assertEquals(cityDTO, result);
    }

    @Test
    void getCityByIdIfExistsShouldReturnEntity() {
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        City result = cityService.getCityByIdIfExists(cityId);

        assertEquals(city, result);
    }

    @Test
    void getCityByIdIfExistsShouldThrowException() {
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cityService.getCityByIdIfExists(cityId));
    }

    @Test
    void updateShouldThrowEntityExistsException() {
        CityDTO updateDTO = new CityDTO(cityId, "updatedCity");

        City existing = new City();
        existing.setName("updatedCity");

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(cityRepository.findByName("updatedCity")).thenReturn(Optional.of(existing));

        assertThrows(EntityExistsException.class, () -> cityService.update(cityId, updateDTO));
    }

    @Test
    void updateShouldUpdateCity() {
        CityDTO updateDTO = new CityDTO(cityId, "updatedCity");

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(cityRepository.findByName("updatedCity")).thenReturn(Optional.empty());
        when(cityMapper.cityToCityDTO(city)).thenReturn(updateDTO);

        CityDTO result = cityService.update(cityId, updateDTO);

        assertEquals("updatedCity", city.getName());
        assertEquals(updateDTO, result);
    }

    @Test
    void deleteShouldCallRepository() {
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        cityService.delete(cityId);

        verify(cityRepository).delete(city);
    }
}