package com.senla.task1.service;

import com.senla.task1.exceptions.OrderStatusException;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository orderStatusRepository;

    @InjectMocks
    private OrderStatusService orderStatusService;

    @Test
    void findByCode_Success() {
        OrderStatus orderStatus = new OrderStatus(1, OrderStatusType.CANCEL, "Отменен");

        when(orderStatusRepository.findByCode(orderStatus.getCode())).thenReturn(Optional.of(orderStatus));

        OrderStatus result = orderStatusService.findByCode(orderStatus.getCode());

        assertEquals(orderStatus, result);
    }

    @Test
    void findByCode_ReturnOrderStatusException() {
        when(orderStatusRepository.findByCode(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderStatusException.class,
                () -> orderStatusService.findByCode(any()));

        assertEquals(OrderStatusException.class, exception.getClass());
    }

    @Test
    void findById_Success() {
        OrderStatus orderStatus = new OrderStatus(1, OrderStatusType.CANCEL, "Отменен");

        when(orderStatusRepository.findById(orderStatus.getId())).thenReturn(Optional.of(orderStatus));

        OrderStatus result = orderStatusService.findById(orderStatus.getId());

        assertNotNull(result);
        assertEquals(orderStatus.getId(), result.getId());
    }

    @Test
    void findById_ThrowOrderStatusException() {
        when(orderStatusRepository.findById(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderStatusException.class,
                () -> orderStatusService.findById(1));

        assertEquals(OrderStatusException.class, exception.getClass());
        assertEquals("Статуса заказа с id 1 нет", exception.getMessage());
    }

    @Test
    void findByCodeString_Success() {
        OrderStatus orderStatus = new OrderStatus(1, OrderStatusType.CANCEL, "Отменен");

        when(orderStatusRepository.findByCodeString("NEW")).thenReturn(Optional.of(orderStatus));

        OrderStatus result = orderStatusService.findByCodeString("NEW");

        assertEquals(orderStatus, result);
    }

    @Test
    void findByCodeString_ThrowOrderStatusException() {
        when(orderStatusRepository.findByCodeString(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderStatusException.class,
                () -> orderStatusService.findByCodeString("NEW"));

        assertEquals(OrderStatusException.class, exception.getClass());
        assertEquals("Статуса заказа с кодом NEW нет", exception.getMessage());
    }

    @Test
    void checkIsOrderStatusExists_ReturnTrue() {
        OrderStatus orderStatus = new OrderStatus(1, OrderStatusType.CANCEL, "Отменен");

        when(orderStatusRepository.checkIsOrderStatusExists(orderStatus.getId())).thenReturn(true);

        boolean result = orderStatusService.checkIsOrderStatusExists(orderStatus.getId());

        assertTrue(result);
    }

    @Test
    void checkIsOrderStatusExists_ReturnFalse() {
        when(orderStatusRepository.checkIsOrderStatusExists(any())).thenReturn(false);

        boolean result = orderStatusService.checkIsOrderStatusExists(any());

        assertFalse(result);
    }
}