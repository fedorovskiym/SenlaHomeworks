package com.senla.task1.service;

import com.senla.task1.config.AutoServiceConfig;
import com.senla.task1.controller.GaragePlaceController;
import com.senla.task1.dto.GaragePlaceDTO;
import com.senla.task1.exceptions.GaragePlaceException;
import com.senla.task1.mapper.GaragePlaceMapper;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.GaragePlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Or;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.cglib.core.Local;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GaragePlaceServiceTest {

    @Mock
    private GaragePlaceRepository garagePlaceRepository;

    @Mock
    private GaragePlaceMapper garagePlaceMapper;

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

    @Test()
    void findPlaceByNumber_ReturnNotFoundExceptionMessage() {
        Exception exception = assertThrows(GaragePlaceException.class,
                () -> this.garagePlaceService.findPlaceByNumber(1));

        assertEquals(GaragePlaceException.class, exception.getClass());
        assertEquals("Место в гараже № 1 не существует", exception.getMessage());
    }

    @Test
    void findFreeGaragePlaces_ReturnListFreeGaragePlaces() {
        List<GaragePlace> garagePlaces = List.of(
                new GaragePlace(1, 1, true),
                new GaragePlace(2, 2, true)
        );

        List<GaragePlaceDTO> garagePlaceDTOs = List.of(
                new GaragePlaceDTO(1, 1, true),
                new GaragePlaceDTO(2, 2, true)
        );

        when(garagePlaceRepository.findFreeGaragePlaces()).thenReturn(garagePlaces);

        when(garagePlaceMapper.garagePlaceToGaragePlaceDTO(garagePlaces.get(0)))
                .thenReturn(garagePlaceDTOs.get(0));
        when(garagePlaceMapper.garagePlaceToGaragePlaceDTO(garagePlaces.get(1)))
                .thenReturn(garagePlaceDTOs.get(1));

        List<GaragePlaceDTO> resultList = garagePlaceService.findFreeGaragePlaces();
        System.out.println(garagePlaceDTOs);
        System.out.println(resultList);
        assertEquals(garagePlaceDTOs, resultList);
    }

    @Test
    void addGaragePlace_Success() {
        GaragePlace garagePlace = new GaragePlace(1);
        garagePlaceRepository.save(garagePlace);
        verify(garagePlaceRepository).save(garagePlace);
    }

    @Test
    void removeGaragePlace_Success() {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        garagePlaceRepository.save(garagePlace);
        garagePlaceRepository.delete(garagePlace);
        verify(garagePlaceRepository).delete(garagePlace);
    }

    @Test
    void removeGaragePlace_ReturnGaragePlaceException() {
        when(garagePlaceRepository.findById(1)).thenReturn(Optional.empty());
        Exception exception = assertThrows(GaragePlaceException.class, () -> this.garagePlaceService.removeGaragePlace(1));
        assertEquals(GaragePlaceException.class, exception.getClass());
        assertEquals( "Места в гараже c id 1 не найдено", exception.getMessage());
    }

    @Test
    void isGaragePlaceAvailable_ReturnTrue() {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        List<Order> orders = List.of();
        boolean available = garagePlaceService.isGaragePlaceAvailable(garagePlace, orders, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        assertEquals(true, available);
    }

    @Test
    void isGaragePlaceAvailable_ReturnFalse() {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCode(OrderStatusType.ACCEPTED);
        Order order = new Order();
        order.setGaragePlace(garagePlace);
        order.setSubmissionDateTime(LocalDateTime.now());
        order.setPlannedCompletionDateTime(LocalDateTime.now().plusMinutes(10));
        order.setStatus(orderStatus);
        boolean available = garagePlaceService.isGaragePlaceAvailable(garagePlace, List.of(order), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        assertEquals(false, available);
    }

    @Test
    void importFromCSV_ReturnFileNotFoundException() {
        String fileName = "test.csv";
        Exception exception = assertThrows(Exception.class, () -> garagePlaceService.importFromCSV(fileName));
        assertTrue(exception.getCause() instanceof FileNotFoundException);
    }

    @Test
    void importFromCSV_SaveSuccess() {
        garagePlaceService.importFromCSV("garage_place_test.csv");
        verify(garagePlaceRepository, times(2)).save(any());
    }

    @Test
    void importFromCSVGaragePlace_UpdateSuccess() {
        doReturn(true).when(garagePlaceRepository).checkIsPlaceNumberExists(1);
        garagePlaceService.importFromCSV("garage_place_test.csv");
        verify(garagePlaceRepository, times(1)).update(any());
    }

    @Test
    void updateGaragePlace_Success() {
        GaragePlace garagePlace = new GaragePlace(1, 1, true);
        garagePlaceService.updateGaragePlace(garagePlace);
        verify(garagePlaceRepository).update(garagePlace);
    }

    @Test
    void exportToCSV_Success() {
        GaragePlace firstGaragePlace = new GaragePlace(1, 1, true);
        GaragePlace secondGaragePlace = new GaragePlace(2, 2, true);
        when(garagePlaceRepository.findAll()).thenReturn(List.of(firstGaragePlace, secondGaragePlace));

        String csv = garagePlaceService.exportToCSV();

        assertTrue(csv.startsWith("id;placeNumber;isEmpty"));
        assertTrue(csv.contains("1;1;true"));
        assertTrue(csv.contains("2;2;true"));
    }

    @Test
    void isGaragePlaceExists_ReturnTrue() {
        when(garagePlaceRepository.checkIsPlaceNumberExists(any())).thenReturn(true);
        boolean result = garagePlaceService.isGaragePlaceExists(1);
        assertTrue(result);
    }

    @Test
    void isGaragePlaceExists_ReturnFalse() {
        when(garagePlaceRepository.checkIsPlaceNumberExists(any())).thenReturn(false);
        boolean result = garagePlaceService.isGaragePlaceExists(1);
        assertFalse(result);
    }
}