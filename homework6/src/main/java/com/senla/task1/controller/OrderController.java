package com.senla.task1.controller;

import com.senla.task1.dto.OrderDTO;
import com.senla.task1.dto.OrderDTORequest;
import com.senla.task1.exceptions.EntityAlreadyExistsException;
import com.senla.task1.exceptions.EntityNotFoundException;
import com.senla.task1.models.enums.OrderStatusType;
import com.senla.task1.service.AutoService;
import com.senla.task1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @PostMapping(value = "/accept/{id}")
    public ResponseEntity<OrderDTO> acceptOrder(@PathVariable("id") Integer id) {
        orderService.acceptOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderById(id));
    }

    @PostMapping(value = "/close/{id}")
    public ResponseEntity<OrderDTO> closeOrder(@PathVariable("id") Integer id) {
        autoService.closeOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderById(id));
    }

    @PostMapping(value = "/cancel/{id}")
    public ResponseEntity<OrderDTO> cancelOrder(@PathVariable("id") Integer id) {
        autoService.cancelOrder(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderById(id));
    }

    @PostMapping(value = "/shift_time")
    public ResponseEntity<List<OrderDTO>> shiftOrdersTime(@RequestParam("hours") Integer hours,@RequestParam("minutes") Integer minutes) {
        orderService.shiftOrdersTime(hours, minutes);
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @DeleteMapping(value = "delete/{id}")
    public ResponseEntity<HttpStatus> deleteOrder(@PathVariable("id") Integer id) {
        autoService.deleteOrder(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/mechanic_id/{id}")
    public ResponseEntity<List<OrderDTO>> findOrderByMechanicId(@PathVariable("id") Integer mechanicId) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderByMechanicId(mechanicId));
    }

    @GetMapping(value = "/status")
    public ResponseEntity<List<OrderDTO>> showOrdersByStatus(@RequestParam("status") String status) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrderByStatus(status));
    }

    @GetMapping(value = "/")
    public ResponseEntity<List<OrderDTO>> showAllOrders() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getAllOrders());
    }

    @GetMapping(value = "/sort/date_of_submission")
    public ResponseEntity<List<OrderDTO>> showSortedOrdersByDateOfSubmission(@RequestParam("flag") Boolean flag) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.sortOrdersByDateOfSubmission(flag));
    }

    @GetMapping(value = "/sort/date_of_completion")
    public ResponseEntity<List<OrderDTO>> showSortedOrdersByDateOfCompletion(@RequestParam("flag") Boolean flag) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.sortOrdersByDateOfCompletion(flag));
    }

    @GetMapping(value = "/sort/price")
    public ResponseEntity<List<OrderDTO>> showSortedOrdersByPrice(@RequestParam("flag") Boolean flag) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.sortOrdersByPrice(flag));
    }

    @GetMapping(value = "/period")
    public ResponseEntity<List<OrderDTO>> showOrdersOverPeriodOfTime(@RequestBody OrderDTORequest orderDTORequest) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findOrdersOverPeriodOfTime(orderDTORequest));
    }

    @GetMapping(value = "/nearest_available_slot")
    public ResponseEntity<?> showNearestAvailableSlot() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.showNearestAvailableDate());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public String handleIllegalArgument(IllegalArgumentException exception) {
        return "Wrong argument: " + exception.getMessage();
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<String> handleAlreadyExists(EntityAlreadyExistsException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFound(EntityNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
