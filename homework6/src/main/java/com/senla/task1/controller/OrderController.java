package com.senla.task1.controller;

import com.senla.task1.dto.OrderDTO;
import com.senla.task1.dto.OrderSearchDTO;
import com.senla.task1.service.AutoService;
import com.senla.task1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    public final AutoService autoService;

    @Autowired
    public OrderController(OrderService orderService, AutoService autoService) {
        this.orderService = orderService;
        this.autoService = autoService;
    }

    @PatchMapping(value = "/{action}/{id}")
    public ResponseEntity<OrderDTO> changeOrderStatus(@PathVariable("action") String action, @PathVariable("id") Integer id) {
        switch (action) {
            case "accept": orderService.acceptOrder(id);
            case "cancel": autoService.cancelOrder(id);
            case "close": autoService.closeOrder(id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderById(id));
    }

    @PostMapping(value = "/shift_time")
    public ResponseEntity<List<OrderDTO>> shiftOrdersTime(@RequestParam("hours") Integer hours, @RequestParam("minutes") Integer minutes) {
        orderService.shiftOrdersTime(hours, minutes);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") Integer id) {
        autoService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<OrderDTO>> showAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping(value = "/nearest_available_slot")
    public ResponseEntity<?> showNearestAvailableSlot() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.showNearestAvailableDate());
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<OrderDTO>> searchOrders(@RequestBody OrderSearchDTO orderSearchDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.searchOrders(orderSearchDTO));
    }
}
