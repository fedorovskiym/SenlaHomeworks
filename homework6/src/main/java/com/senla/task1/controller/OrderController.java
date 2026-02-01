package com.senla.task1.controller;

import com.senla.task1.models.enums.OrderSortType;
import com.senla.task1.models.enums.OrderStatus;
import com.senla.task1.service.AutoService;
import com.senla.task1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController {

    private final OrderService orderService;
    public final AutoService autoService;

    @Autowired
    public OrderController(OrderService orderService, AutoService autoService) {
        this.orderService = orderService;
        this.autoService = autoService;
    }

    public void acceptOrder(int id) {
        orderService.acceptOrder(id);
    }

    public void closeOrder(int id) {
        autoService.closeOrder(id);
    }

    public void cancelOrder(int id) {
        autoService.cancelOrder(id);
    }

    public void shiftOrdersTime(int hours, int minutes) {
        orderService.shiftOrdersTime(hours, minutes);
    }

    public void deleteOrder(int id) {
        autoService.deleteOrder(id);
    }

    public void findOrderByMechanicId(int mechanicId) {
        orderService.findOrderByMechanicId(mechanicId);
    }

    public void showOrdersByStatus(OrderStatus status) {
        orderService.findOrderByStatus(status);
    }

    public void showAllOrders() {
        orderService.showOrders(orderService.findAllOrders());
    }

    public void showSortedOrdersByDateOfSubmission(boolean flag) {
        orderService.sortOrdersByDateOfSubmission(flag);
    }

    public void showSortedOrdersByDateOfCompletion(boolean flag) {
        orderService.sortOrdersByDateOfCompletion(flag);
    }

    public void showSortedOrdersByPrice(boolean flag) {
        orderService.sortOrdersByPrice(flag);
    }

    public void showOrdersOverPeriodOfTime(int fromYear, int fromMonth, int fromDay, int toYear,
                                           int toMonth, int toDay, OrderSortType sortType, boolean flag) {
        orderService.findOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay, toYear, toMonth, toDay, sortType, flag);
    }

    public void showNearestAvailableSlot() {
        orderService.showNearestAvailableDate();
    }
}
