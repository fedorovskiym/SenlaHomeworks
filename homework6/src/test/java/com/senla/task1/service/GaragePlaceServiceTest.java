package com.senla.task1.service;

import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.dto.GaragePlaceDTO;
import com.senla.task1.mapper.GaragePlaceMapper;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.repository.GaragePlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GaragePlaceServiceTest {

    @Mock
    private GaragePlaceRepository garagePlaceRepository;

    @Mock
    private GaragePlaceMapper garagePlaceMapper;

    @Mock
    private AutoServiceConfig autoServiceConfig;

    @InjectMocks
    private GaragePlaceService garagePlaceService;


    @Test
    void findAllGaragePlace_ReturnList() {
        List<GaragePlace> garagePlaces = List.of(new GaragePlace(1, 1, true),
                new GaragePlace(2, 2, false), new GaragePlace(3, 3, true));
        when(garagePlaceRepository.findAll()).thenReturn(garagePlaces);

        List<GaragePlace> resultList = this.garagePlaceService.findAllGaragePlace();

        assertEquals(garagePlaces, resultList);
    }

    @Test
    void findPlaceByNumber_ReturnGaragePlace() {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        when(garagePlaceRepository.findByPlaceNumber(garagePlace.getPlaceNumber())).thenReturn(Optional.of(garagePlace));

        GaragePlace result = this.garagePlaceService.findPlaceByNumber(garagePlace.getPlaceNumber());

        assertEquals(garagePlace, result);
    }

    @Test
    void findPlaceByNumber_ReturnNotFoundExceptionMessage() {

    }

    @Test
    void findFreeGaragePlaces() {
    }

    @Test
    void addGaragePlace() {
    }

    @Test
    void removeGaragePlace() {
    }

    @Test
    void isGaragePlaceAvailable() {
    }

    @Test
    void importFromCSV() {
    }

    @Test
    void updateGaragePlace() {
    }

    @Test
    void exportToCSV() {
    }

    @Test
    void isGaragePlaceExists() {
    }
}