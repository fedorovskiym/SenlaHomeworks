package com.senla.task1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senla.task1.dto.OrderDTO;
import com.senla.task1.dto.OrderSearchDTO;
import com.senla.task1.service.AutoService;
import com.senla.task1.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @Mock
    private AutoService autoService;

    private OrderController orderController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderController = new OrderController(orderService, autoService);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    }

    @Test
    void changeOrderStatus_ReturnHttpStatusOkAndOrderDTO_ActionAccept() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, "Принят", null, null, null, null, null, 3000.0);

        doNothing().when(orderService).acceptOrder(orderDTO.id());
        doReturn(orderDTO).when(orderService).findOrderById(orderDTO.id());

        mockMvc.perform(patch("/order/{action}/{id}", "accept", orderDTO.id()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var objectResponse = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructType(OrderDTO.class));
                    assertEquals(orderDTO, objectResponse);
                });
    }

    @Test
    void changeOrderStatus_ReturnHttpStatusOkAndOrderDTO_ActionCancel() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, "Отменен", null, null, null, null, null, 3000.0);

        doNothing().when(autoService).cancelOrder(orderDTO.id());
        doReturn(orderDTO).when(orderService).findOrderById(orderDTO.id());

        mockMvc.perform(patch("/order/{action}/{id}", "cancel", orderDTO.id()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var objectResponse = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructType(OrderDTO.class));
                    assertEquals(orderDTO, objectResponse);
                });
    }

    @Test
    void changeOrderStatus_ReturnHttpStatusOkAndOrderDTO_ActionClose() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, "Выполнен", null, null, null, null, null, 3000.0);

        doNothing().when(autoService).closeOrder(orderDTO.id());
        doReturn(orderDTO).when(orderService).findOrderById(orderDTO.id());

        mockMvc.perform(patch("/order/{action}/{id}", "close", orderDTO.id()))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var objectResponse = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructType(OrderDTO.class));
                    assertEquals(orderDTO, objectResponse);
                });
    }

    @Test
    void shiftOrdersTime_ReturnHttpStatusOkAndListOrderDTO() throws Exception {
        Integer hours = 1;
        Integer minutes = 0;
        List<OrderDTO> orderList = List.of(new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, null, null, null, null, null, null, 3000.0));
        doNothing().when(orderService).shiftOrdersTime(hours, minutes);
        doReturn(orderList).when(orderService).getAllOrders();

        mockMvc.perform(post("/order/shift_time")
                        .param("hours", String.valueOf(hours))
                        .param("minutes", String.valueOf(minutes)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseList = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDTO.class));
                    assertEquals(orderList, responseList);
                });
    }

    @Test
    void deleteOrder_ReturnHttpStatusNoContent() throws Exception {
        doNothing().when(autoService).deleteOrder(1);
        mockMvc.perform(delete("/order/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    void showAllOrders_ReturnHttpStatusOkAndListOrderDTO() throws Exception {
        List<OrderDTO> orderList = List.of(new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, null, null, null, null, null, null, 3000.0));

        doReturn(orderList).when(orderService).getAllOrders();

        mockMvc.perform(get("/order/"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResponse = result.getResponse().getContentAsString();
                    var responseList = objectMapper.readValue(jsonResponse,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDTO.class));
                    assertEquals(orderList, responseList);
                });
    }

    @Test
    void showNearestAvailableSlot_ReturnHttpStatusOkAndReturnLocalDateTime() throws Exception {
        LocalDateTime nearestAvailableSlot = LocalDateTime.now();
        doReturn(nearestAvailableSlot).when(orderService).showNearestAvailableDate();

        mockMvc.perform(get("/order/nearest_available_slot"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResult = result.getResponse().getContentAsString();
                    objectMapper.registerModule(new JavaTimeModule());
                    var responseObject = objectMapper.readValue(jsonResult, LocalDateTime.class);
                    assertEquals(nearestAvailableSlot, responseObject);
                });
    }

    @Test
    void searchOrders_ReturnHttpStatusOkAndListOrderDTO() throws Exception {
        OrderSearchDTO orderSearchDTO = new OrderSearchDTO(1, null, null, null, null,
                null, null, null, null, null, null, null, null);
        List<OrderDTO> orderList = List.of(new OrderDTO(1, "testName", 1, "name1", "surname1",
                1, null, null, null, null, null, null, 3000.0));

        doReturn(orderList).when(orderService).searchOrders(orderSearchDTO);

        mockMvc.perform(get("/order/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderSearchDTO)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String jsonResult = result.getResponse().getContentAsString();
                    var responseObject = objectMapper.readValue(jsonResult,
                            objectMapper.getTypeFactory().constructCollectionType(List.class, OrderDTO.class));
                    assertEquals(orderList, responseObject);
                });
    }
}