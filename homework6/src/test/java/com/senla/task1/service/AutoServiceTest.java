package com.senla.task1.service;

import com.senla.task1.dto.AutoServiceRequestDTO;
import com.senla.task1.dto.OrderDTO;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.mapper.OrderMapper;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoServiceTest {

    @Mock
    private OrderService orderService;

    @Mock
    private MechanicService mechanicService;

    @Mock
    private GaragePlaceService garagePlaceService;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private AutoService autoService;

    private Mechanic mechanic;
    private GaragePlace garagePlace;
    private OrderStatus orderStatus;
    private Order order;

    @BeforeEach
    void setUp() {
        mechanic = new Mechanic();
        mechanic.setId(1);
        mechanic.setBusy(false);

        garagePlace = new GaragePlace();
        garagePlace.setPlaceNumber(1);
        garagePlace.setEmpty(true);

        orderStatus = new OrderStatus();
        orderStatus.setId(1);
        orderStatus.setCode(OrderStatusType.WAITING);

        order = new Order();
        order.setId(1);
        order.setMechanic(mechanic);
        order.setGaragePlace(garagePlace);
        order.setStatus(orderStatus);
    }

    @Test
    void createOrder_CreateOrderSuccess() {
        AutoServiceRequestDTO requestDTO = new AutoServiceRequestDTO("CarModel", 1, 1, 2000.0, 1, 0);
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(mechanicService.findMechanicById(1)).thenReturn(mechanic);
        when(garagePlaceService.findPlaceByNumber(1)).thenReturn(garagePlace);
        when(orderStatusService.findByCode(OrderStatusType.WAITING)).thenReturn(orderStatus);
        doNothing().when(orderService).addOrder(any());
        when(orderMapper.orderToOrderDTO(any())).thenReturn(orderDTO);

        autoService.createOrder(requestDTO);

        assertTrue(mechanic.getIsBusy());
        assertFalse(garagePlace.isEmpty());
        verify(orderService).addOrder(any());
        verify(mechanicService).updateMechanic(mechanic);
        verify(garagePlaceService).updateGaragePlace(garagePlace);
    }

    @Test
    void createOrder_ThrowConflictWhenMechanicOrGaragePlaceBusy() {
        mechanic.setBusy(true);
        AutoServiceRequestDTO requestDTO = new AutoServiceRequestDTO("CarModel", 1, 1, 2000.0, 1, 0);

        when(mechanicService.findMechanicById(1)).thenReturn(mechanic);
        when(garagePlaceService.findPlaceByNumber(1)).thenReturn(garagePlace);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> autoService.createOrder(requestDTO));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Mechanic or garage place is busy!", exception.getReason());
        verify(orderService, never()).addOrder(any());
    }

    @Test
    void getAvailableSlot_ReturnMinAvailableMechanicAndGaragePlace() {
        when(orderService.findAllOrders()).thenReturn(List.of(order));

        when(mechanicService.findAllMechanic()).thenReturn(List.of(mechanic));
        when(mechanicService.isMechanicAvailable(any(), anyList(), any(), any())).thenReturn(true);

        when(garagePlaceService.findAllGaragePlace()).thenReturn(List.of(garagePlace));
        when(garagePlaceService.isGaragePlaceAvailable(any(), anyList(), any(), any())).thenReturn(true);

        int availableSlots = autoService.getAvailableSlot(2026, 3, 30);

        assertEquals(1, availableSlots);
    }

    @Test
    void importFromCSV_ReturnFilaNotFoundException() {
        String fileName = "test.csv";
        Exception exception = assertThrows(Exception.class, () -> autoService.importFromCSV(fileName));
        assertTrue(exception.getCause() instanceof FileNotFoundException);
    }

    @Test
    void importFromCsv_SaveSuccess() {
        orderStatus.setCode(OrderStatusType.WAITING);

        when(mechanicService.isMechanicExists(1)).thenReturn(true);
        when(mechanicService.findMechanicById(1)).thenReturn(mechanic);

        when(garagePlaceService.isGaragePlaceExists(1)).thenReturn(true);
        when(garagePlaceService.findPlaceByNumber(1)).thenReturn(garagePlace);

        when(orderStatusService.checkIsOrderStatusExists(1)).thenReturn(true);
        when(orderStatusService.findById(1)).thenReturn(orderStatus);

        when(orderService.isOrdersExists(1)).thenReturn(false);

        doNothing().when(orderService).addOrder(any(Order.class));
        doNothing().when(mechanicService).updateMechanic(any(Mechanic.class));
        doNothing().when(garagePlaceService).updateGaragePlace(any(GaragePlace.class));

        mechanic.setBusy(false);
        garagePlace.setEmpty(true);

        autoService.importFromCSV("order_test.csv");

        verify(mechanicService).updateMechanic(mechanic);
        verify(garagePlaceService).updateGaragePlace(garagePlace);
        verify(orderService).addOrder(any());
    }

    @Test
    void exportOrdersToCSV() {
        when(mechanicService.isMechanicExists(1)).thenReturn(true);
        when(mechanicService.findMechanicById(1)).thenReturn(mechanic);

        when(garagePlaceService.isGaragePlaceExists(1)).thenReturn(true);
        when(garagePlaceService.findPlaceByNumber(1)).thenReturn(garagePlace);

        when(orderStatusService.checkIsOrderStatusExists(1)).thenReturn(true);
        when(orderStatusService.findById(1)).thenReturn(orderStatus);

        when(orderService.isOrdersExists(1)).thenReturn(true);
        when(orderService.findOrderByIdIsExists(order.getId())).thenReturn(Optional.ofNullable(order));

        doNothing().when(orderService).update(any());
        doNothing().when(mechanicService).updateMechanic(any());
        doNothing().when(garagePlaceService).updateGaragePlace(any());

        autoService.importFromCSV("order_test.csv");

        verify(orderService).update(any());
        verify(mechanicService).updateMechanic(mechanic);
        verify(garagePlaceService).updateGaragePlace(garagePlace);
    }

    @Test
    void updateOrder() {
        Mechanic newMechanic = new Mechanic();
        newMechanic.setId(2);
        newMechanic.setBusy(false);

        GaragePlace newGarage = new GaragePlace();
        newGarage.setPlaceNumber(2);
        newGarage.setEmpty(true);

        when(orderService.findOrderByIdIsExists(order.getId())).thenReturn(Optional.of(order));
        doNothing().when(orderService).update(any(Order.class));

        autoService.updateOrder(order.getId(), "testCar", newMechanic, newGarage,
                orderStatus, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null,
                null, Duration.ofHours(3), 2000.0
        );

        assertEquals("testCar", order.getCarName());
        assertEquals(newMechanic, order.getMechanic());
        assertEquals(newGarage, order.getGaragePlace());

        assertFalse(mechanic.getIsBusy());
        assertTrue(newMechanic.getIsBusy());

        assertTrue(garagePlace.isEmpty());
        assertFalse(newGarage.isEmpty());

        verify(orderService).update(order);
    }

    @Test
    void deleteOrder_Success() {
        doNothing().when(orderService).deleteOrder(order.getId());
        autoService.deleteOrder(order.getId());
        verify(orderService).deleteOrder(order.getId());
    }

    @Test
    void closeOrder_Success() {
        OrderRepository orderRepository = mock(OrderRepository.class);

        when(orderService.getOrderDAO()).thenReturn(orderRepository);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderStatus.setCode(OrderStatusType.DONE);
        when(orderStatusService.findByCode(OrderStatusType.DONE)).thenReturn(orderStatus);

        doNothing().when(orderService).update(any());
        doNothing().when(orderService).acceptOrder(anyInt());

        autoService.closeOrder(order.getId());

        assertEquals(OrderStatusType.DONE, order.getStatus().getCode());
        verify(orderService).update(order);
        verify(orderService).acceptOrder(order.getId() + 1);
    }

    @Test
    void closeOrder_ThrowOrderException() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderService.getOrderDAO()).thenReturn(orderRepository);

        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class, () -> autoService.closeOrder(1));

        assertEquals(OrderException.class, exception.getClass());
        assertEquals("Заказ с №1 не найден", exception.getMessage());
    }

    @Test
    void cancelOrder() {
        OrderRepository orderRepository = mock(OrderRepository.class);

        when(orderService.getOrderDAO()).thenReturn(orderRepository);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderStatus.setCode(OrderStatusType.CANCEL);
        when(orderStatusService.findByCode(OrderStatusType.CANCEL)).thenReturn(orderStatus);

        doNothing().when(orderService).update(any());

        autoService.cancelOrder(order.getId());

        assertEquals(OrderStatusType.CANCEL, order.getStatus().getCode());
    }

    @Test
    void cancelOrder_ThrowOrderException() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        when(orderService.getOrderDAO()).thenReturn(orderRepository);

        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class, () -> autoService.cancelOrder(1));

        assertEquals(OrderException.class, exception.getClass());
        assertEquals("Заказ с №1 не найден", exception.getMessage());
    }
}