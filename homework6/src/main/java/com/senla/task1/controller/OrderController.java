package com.senla.task1.controller;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.models.enums.OrderSortType;
import com.senla.task1.models.enums.OrderStatus;
import com.senla.task1.service.OrderService;

public class OrderController {

    private final OrderService orderService;

    @Inject
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Контроллер заказов создался");
    }

    public void acceptOrder(int id) {
        orderService.acceptOrder(id);
    }

    public void closeOrder(int id) {
        orderService.closeOrder(id);
    }

    public void cancelOrder(int id) {
        orderService.cancelOrder(id);
    }

    public void shiftOrdersTime(int hours, int minutes) {
        orderService.shiftOrdersTime(hours, minutes);
    }

    public void deleteOrder(int id) {
        orderService.deleteOrder(id);
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
