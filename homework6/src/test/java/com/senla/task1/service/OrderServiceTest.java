package com.senla.task1.service;

import com.senla.task1.dto.OrderDTO;
import com.senla.task1.dto.OrderSearchDTO;
import com.senla.task1.exceptions.OrderException;
import com.senla.task1.mapper.OrderMapper;
import com.senla.task1.models.Order;
import com.senla.task1.models.OrderStatus;
import com.senla.task1.models.enums.OrderSortType;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    @Spy
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1);
        order.setDuration(Duration.ofHours(1));
    }

    @Test
    void findAllOrders_ReturnListOrder() {
        List<Order> orderList = List.of(order);

        when(orderRepository.findAll()).thenReturn(orderList);
        List<Order> resultList = orderService.findAllOrders();

        assertEquals(orderList, resultList);
    }

    @Test
    void getAllOrders_ReturnListOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderService.findAllOrders()).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);
        List<OrderDTO> resultList = orderService.getAllOrders();

        assertEquals(List.of(orderDTO), resultList);
    }

    @Test
    void addOrder_WithEndDateTimeLastOrder() {
        LocalDateTime lastTime = LocalDateTime.now();

        Order lastOrder = new Order();
        lastOrder.setEndDateTime(lastTime);

        when(orderRepository.getEndDateTimeLastActiveOrder()).thenReturn(Optional.of(lastOrder));
        orderService.addOrder(order);

        verify(orderRepository).save(order);
        assertEquals(lastTime, order.getPlannedCompletionDateTime());
        assertEquals(lastTime.plus(order.getDuration()), order.getEndDateTime());
    }

    @Test
    void addOrder_WithoutEndDateTimeLastOrder() {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCode(OrderStatusType.WAITING);
        order.setStatus(orderStatus);
        when(orderRepository.getEndDateTimeLastActiveOrder()).thenReturn(Optional.empty());
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));

        orderService.addOrder(order);

        verify(orderRepository).save(order);
        assertNotNull(order.getCompletionDateTime());
        assertNotNull(order.getEndDateTime());
    }

    @Test
    void acceptOrder_ThrowOrderException() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class, () -> orderService.acceptOrder(1));

        assertEquals(OrderException.class, exception.getClass());
        assertEquals("Заказ с №1 не найден", exception.getMessage());
    }

    @Test
    void acceptOrder_DoNothingIfOrderAlreadyAccept() {
        OrderStatus status = new OrderStatus();
        status.setCode(OrderStatusType.ACCEPTED);
        order.setStatus(status);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        orderService.acceptOrder(1);

        verify(orderRepository, never()).update(order);
    }

    @Test
    void acceptOrder_AcceptAndUpdateOrder() {
        OrderStatus status = new OrderStatus();
        status.setCode(OrderStatusType.WAITING);
        order.setStatus(status);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.getEndDateTimeLastActiveOrder()).thenReturn(Optional.empty());
        orderService.acceptOrder(order.getId());

        verify(orderRepository).update(order);
        assertNotNull(order.getCompletionDateTime());
        assertNotNull(order.getEndDateTime());
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        orderService.deleteOrder(order.getId());

        verify(orderRepository).delete(order);
    }

    @Test
    void deleteOrder_ThrowOrderException() {
        when(orderRepository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class,
                () -> orderService.deleteOrder(1));

        assertEquals(OrderException.class, exception.getClass());
        assertEquals("Заказ с №1 не найден", exception.getMessage());
    }

    @Test
    void shiftOrdersTime_UpdateOrder() {
        order.setEndDateTime(LocalDateTime.now());
        when(orderRepository.findAll()).thenReturn(List.of(order));

        orderService.shiftOrdersTime(1, 0);

        verify(orderRepository).update(order);
        assertEquals(Duration.ofHours(2).toHours(), order.getDuration().toHours());
    }

    @Test
    void findOrderByMechanicId_ReturnListOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderRepository.findOrderByMechanicId(any())).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.findOrderByMechanicId(any());

        assertEquals(List.of(orderDTO), result);
    }

    @Test
    void getEndDateTimeLastOrder_ReturnTime() {
        LocalDateTime time = LocalDateTime.now();
        order.setEndDateTime(time);

        when(orderRepository.getEndDateTimeLastActiveOrder()).thenReturn(Optional.of(order));
        LocalDateTime result = orderService.getEndDateTimeLastOrder();

        assertEquals(time, result);
    }

    @Test
    void getEndDateTimeLastOrder_ReturnNull() {
        when(orderRepository.getEndDateTimeLastActiveOrder()).thenReturn(Optional.empty());

        assertNull(orderService.getEndDateTimeLastOrder());
    }

    @Test
    void findOrderByStatus_ReturnListDTO() {
        String statusCode = "ACCEPTED";

        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setCode(OrderStatusType.ACCEPTED);

        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);


        when(orderStatusService.findByCodeString(statusCode)).thenReturn(orderStatus);
        when(orderRepository.findOrderByStatus(orderStatus)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.findOrderByStatus(statusCode);

        assertEquals(List.of(orderDTO), result);
        verify(orderStatusService).findByCodeString(statusCode);
        verify(orderRepository).findOrderByStatus(orderStatus);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void sortOrdersByDateOfSubmission_ReturnListOrderDTO() {
        Boolean flag = true;

        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderRepository.sortBy(OrderSortType.DATE_OF_SUBMISSION.getDisplayName(), flag)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.sortOrdersByDateOfSubmission(flag);

        assertEquals(List.of(orderDTO), result);
        verify(orderRepository).sortBy(OrderSortType.DATE_OF_SUBMISSION.getDisplayName(), flag);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void sortOrdersByDateOfCompletion_ReturnListOrderDTO() {
        Boolean flag = true;

        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderRepository.sortBy(OrderSortType.DATE_OF_COMPLETION.getDisplayName(), flag)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.sortOrdersByDateOfCompletion(flag);

        assertEquals(List.of(orderDTO), result);
        verify(orderRepository).sortBy(OrderSortType.DATE_OF_COMPLETION.getDisplayName(), flag);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void sortOrdersByPrice_ReturnListOrderDTO() {
        Boolean flag = true;

        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderRepository.sortBy(OrderSortType.PRICE.getDisplayName(), flag)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.sortOrdersByPrice(flag);

        assertEquals(List.of(orderDTO), result);
        verify(orderRepository).sortBy(OrderSortType.PRICE.getDisplayName(), flag);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void findOrdersOverPeriodOfTime() {
        int fromYear = 2026;
        int fromMonth = 1;
        int fromDay = 1;
        int toYear = 2026;
        int toMonth = 3;
        int toDay = 31;
        String sortType = String.valueOf(OrderSortType.PRICE);
        boolean flag = true;

        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        LocalDateTime startTime = LocalDateTime.of(fromYear, fromMonth, fromDay, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(toYear, toMonth, toDay, 23, 59);

        when(orderRepository.findOrderOverPeriodOfTime(startTime, endTime, sortType, flag)).thenReturn(List.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        List<OrderDTO> result = orderService.findOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay,
                toYear, toMonth, toDay,
                sortType, flag
        );

        assertEquals(List.of(orderDTO), result);

        verify(orderRepository).findOrderOverPeriodOfTime(startTime, endTime, sortType, flag);
        verify(orderMapper).orderToOrderDTO(order);
    }

    @Test
    void showNearestAvailableDate() {
        LocalDateTime lastOrderEndTime = LocalDateTime.of(2026, 3, 30, 12, 0);

        doReturn(lastOrderEndTime).when(orderService).getEndDateTimeLastOrder();
        LocalDateTime result = this.orderService.showNearestAvailableDate();

        assertEquals(lastOrderEndTime, result);
        verify(orderService).getEndDateTimeLastOrder();
    }

    @Test
    void findOrderById_ReturnOrderDTO() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        when(orderRepository.findById(1)).thenReturn(Optional.of(order));
        when(orderMapper.orderToOrderDTO(order)).thenReturn(orderDTO);

        OrderDTO result = orderService.findOrderById(1);

        assertEquals(orderDTO, result);
    }

    @Test
    void findOrderById_ThrowOrderException() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(OrderException.class,
                () -> orderService.findOrderById(order.getId()));

        assertEquals(OrderException.class, exception.getClass());
        assertEquals("Заказ с id - 1 не найден", exception.getMessage());
    }

    @Test
    void findOrderByIdIsExists() {
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        Optional<Order> result = orderService.findOrderByIdIsExists(order.getId());

        assertEquals(Optional.of(order), result);
    }

    @Test
    void isOrdersExists_ReturnTrue() {
        when(orderRepository.checkIsOrderExists(order.getId())).thenReturn(true);

        boolean result = orderService.isOrdersExists(order.getId());

        assertTrue(result);
    }

    @Test
    void isOrderExists_ReturnFalse() {
        when(orderRepository.checkIsOrderExists(order.getId())).thenReturn(false);

        boolean result = orderService.isOrdersExists(order.getId());

        assertFalse(result);
    }

    @Test
    void update() {
        doNothing().when(orderRepository).update(order);
        orderService.update(order);
        verify(orderRepository).update(order);
    }

    @Test
    void searchOrders_ByMechanicId_CallFindOrderByMechanicId() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);
        OrderSearchDTO searchDTO = new OrderSearchDTO(1, null, null, true,
                false, false, false,
                null, null, null, null, null, null);

        doReturn(List.of(orderDTO)).when(orderService).findOrderByMechanicId(searchDTO.mechanicId());

        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).findOrderByMechanicId(searchDTO.mechanicId());
    }

    @Test
    void searchOrders_ByStatus_CallFindOrderByStatus() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);
        OrderSearchDTO searchDTO = new OrderSearchDTO(null, OrderStatusType.ACCEPTED.toString(), null, true,
                false, false, false,
                null, null, null, null, null, null);

        doReturn(List.of(orderDTO)).when(orderService).findOrderByStatus(searchDTO.status());

        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).findOrderByStatus(searchDTO.status());
    }

    @Test
    void searchOrders_BySubmissionDateTime_CallSortOrdersByDateOfSubmission() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);
        OrderSearchDTO searchDTO = new OrderSearchDTO(null, null, null, true,
                true, null, null,
                null, null, null, null, null, null);

        doReturn(List.of(orderDTO)).when(orderService).sortOrdersByDateOfSubmission(searchDTO.flag());

        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).sortOrdersByDateOfSubmission(searchDTO.flag());
    }

    @Test
    void searchOrders_ByCompletionDateTime_CallSortOrdersByDateOfCompletion() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);

        OrderSearchDTO searchDTO = new OrderSearchDTO(null, null, null, true,
                null, true, null,
                null, null, null, null, null, null);

        doReturn(List.of(orderDTO)).when(orderService).sortOrdersByDateOfCompletion(searchDTO.flag());
        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).sortOrdersByDateOfCompletion(searchDTO.flag());
    }

    @Test
    void searchOrders_ByPrice_CallSortOrdersByPrice() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);
        OrderSearchDTO searchDTO = new OrderSearchDTO(null, null, null, true,
                null, null, true,
                null, null, null, null, null, null);


        doReturn(List.of(orderDTO)).when(orderService).sortOrdersByPrice(searchDTO.flag());

        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).sortOrdersByPrice(searchDTO.flag());
    }

    @Test
    void searchOrders_ByPeriod_CallFindOrdersOverPeriodOfTime() {
        OrderDTO orderDTO = new OrderDTO(1, null, null, null, null,
                null, null, null, null, null, null,
                Duration.ofHours(1), null);
        OrderSearchDTO searchDTO = new OrderSearchDTO(null, null, OrderSortType.PRICE.toString(), true,
                null, null, null,
                2026, 3, 1, 2026, 3, 30);

        doReturn(List.of(orderDTO)).when(orderService).findOrdersOverPeriodOfTime(
                searchDTO.fromYear(), searchDTO.fromMonth(), searchDTO.fromDay(),
                searchDTO.toYear(), searchDTO.toMonth(), searchDTO.toDay(),
                searchDTO.sortType(), searchDTO.flag()
        );

        List<OrderDTO> result = orderService.searchOrders(searchDTO);

        assertEquals(List.of(orderDTO), result);
        verify(orderService).findOrdersOverPeriodOfTime(
                searchDTO.fromYear(), searchDTO.fromMonth(), searchDTO.fromDay(),
                searchDTO.toYear(), searchDTO.toMonth(), searchDTO.toDay(),
                searchDTO.sortType(), searchDTO.flag()
        );
    }
}
