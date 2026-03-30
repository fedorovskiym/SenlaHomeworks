package com.senla.task1.service;

import com.senla.task1.dto.MechanicDTO;
import com.senla.task1.exceptions.MechanicException;
import com.senla.task1.mapper.MechanicMapper;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.MechanicSortType;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.MechanicRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MechanicServiceTest {

    @Mock
    private MechanicRepository mechanicRepository;

    @Mock
    private MechanicMapper mechanicMapper;

    @InjectMocks
    private MechanicService mechanicService;

    @Test
    void findAllMechanic_ReturnListMechanicDTO() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        MechanicDTO mechanicDTO = new MechanicDTO(1, "name1", "surname1", 10.0, true);

        doReturn(List.of(mechanic)).when(mechanicRepository).findAll();
        when(mechanicMapper.mechanicToMechanicDTO(mechanic)).thenReturn(mechanicDTO);

        List<MechanicDTO> result = mechanicService.findAllMechanicDTO();

        assertEquals(List.of(mechanicDTO), result);
        verify(mechanicMapper).mechanicToMechanicDTO(mechanic);
    }

    @Test
    void findAllMechanic_ReturnListMechanic() {
        List<Mechanic> mechanicList = List.of(new Mechanic(1, "name1", "surname1", 10.0, true));
        when(mechanicRepository.findAll()).thenReturn(mechanicList);

        List<Mechanic> result = mechanicService.findAllMechanic();

        assertEquals(mechanicList, result);
        verify(mechanicRepository).findAll();
    }

    @Test
    void addMechanic_Success() {
        String name = "name1";
        String surname = "surname1";
        Double experienceYears = 10.0;

        mechanicService.addMechanic(name, surname, experienceYears);

        verify(mechanicRepository).save(any());
    }

    @Test
    void removeMechanicById_Success() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);

        when(mechanicRepository.findById(any())).thenReturn(Optional.of(mechanic));
        doNothing().when(mechanicRepository).delete(mechanic);
        mechanicService.removeMechanicById(mechanic.getId());

        verify(mechanicRepository).delete(mechanic);
    }

    @Test
    void findMechanicById_ThrowMechanicExceptionAndMessage() {
        when(mechanicRepository.findById(1)).thenReturn(Optional.empty());
        Exception exception = assertThrows(MechanicException.class, () -> mechanicService.findMechanicById(1));
        assertEquals(MechanicException.class, exception.getClass());
        assertEquals("Механик с id - 1 не существует", exception.getMessage());
    }

    @Test
    void findMechanicById_Success() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        when(mechanicRepository.findById(mechanic.getId())).thenReturn(Optional.of(mechanic));
        Mechanic result = mechanicService.findMechanicById(mechanic.getId());

        assertEquals(mechanic, result);
        verify(mechanicRepository).findById(mechanic.getId());
    }

    @Test
    void showSortedMechanicByAlphabet_ReturnSortedList() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        MechanicDTO mechanicDTO = new MechanicDTO(1, "name1", "surname1", 10.0, true);

        doReturn(List.of(mechanic)).when(mechanicRepository).sortBy(MechanicSortType.ALPHABET.getDisplayName(), true);

        when(mechanicMapper.mechanicToMechanicDTO(mechanic)).thenReturn(mechanicDTO);

        List<MechanicDTO> result = mechanicService.showSortedMechanicByAlphabet(true);

        assertEquals(List.of(mechanicDTO), result);
        verify(mechanicMapper).mechanicToMechanicDTO(mechanic);
    }

    @Test
    void showSortedMechanicByBusy_ReturnSortedList() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        MechanicDTO mechanicDTO = new MechanicDTO(1, "name1", "surname1", 10.0, true);

        doReturn(List.of(mechanic)).when(mechanicRepository).sortBy(MechanicSortType.BUSY.getDisplayName(), true);

        when(mechanicMapper.mechanicToMechanicDTO(mechanic)).thenReturn(mechanicDTO);

        List<MechanicDTO> result = mechanicService.showSortedMechanicByBusy();

        assertEquals(List.of(mechanicDTO), result);
        verify(mechanicMapper).mechanicToMechanicDTO(mechanic);
    }

    @Test
    void isMechanicAvailable_ReturnTrue() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        List<Order> orders = List.of();
        boolean available = mechanicService.isMechanicAvailable(mechanic, orders, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        assertEquals(true, available);
    }

    @Test
    void isMechanicAvailable_ReturnFalse() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCode(OrderStatusType.ACCEPTED);
        Order order = new Order();
        order.setMechanic(mechanic);
        order.setSubmissionDateTime(LocalDateTime.now());
        order.setPlannedCompletionDateTime(LocalDateTime.now().plusMinutes(10));
        order.setStatus(orderStatus);
        boolean available = mechanicService.isMechanicAvailable(mechanic, List.of(order), LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        assertEquals(false, available);
    }

    @Test
    void importFromCSV_ReturnFileNotFoundException() {
        String fileName = "test.csv";
        Exception exception = assertThrows(Exception.class, () -> mechanicService.importFromCSV(fileName));
        assertTrue(exception.getCause() instanceof FileNotFoundException);
    }

    @Test
    void importFromCSV_SaveSuccess() {
        mechanicService.importFromCSV("mechanic_test.csv");
        verify(mechanicRepository, times(1)).save(any());
    }

    @Test
    void importFromCSV_UpdateSuccess() {
        when(mechanicService.isMechanicExists(any())).thenReturn(true);
        mechanicService.importFromCSV("mechanic_test.csv");
        verify(mechanicRepository, times(1)).update(any());
    }

    @Test
    void exportToCSV_Success() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);

        when(mechanicRepository.findAll()).thenReturn(List.of(mechanic));

        String csv = mechanicService.exportToCSV();

        assertTrue(csv.startsWith("id;name;surname;experience_years;is_busy"));
        assertTrue(csv.contains("1;name1;surname1;10,00;true"));
    }

    @Test
    void updateMechanic_Success() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        mechanicService.updateMechanic(mechanic);
        verify(mechanicRepository).update(mechanic);
    }

    @Test
    void isMechanicExists_ReturnTrue() {
        Mechanic mechanic = new Mechanic(1, "name1", "surname1", 10.0, true);
        when(mechanicRepository.checkIsMechanicExists(mechanic.getId())).thenReturn(true);
        boolean result = mechanicService.isMechanicExists(mechanic.getId());
        verify(mechanicRepository).checkIsMechanicExists(mechanic.getId());
        assertTrue(result);
    }

    @Test
    void isMechanicExists_ReturnFalse() {
        when(mechanicRepository.checkIsMechanicExists(any())).thenReturn(false);
        boolean result = mechanicService.isMechanicExists(any());
        verify(mechanicRepository).checkIsMechanicExists(any());
        assertFalse(result);
    }
}