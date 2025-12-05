package com.senla.task1.controller;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.models.enums.SortType;
import com.senla.task1.service.OrderService;

public class OrderController {

    @Inject
    private OrderService orderService;

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

    public void showOrdersByStatus(String status) {
        orderService.showOrders(status);
    }

    public void showAllOrders() {
        orderService.showOrders();
    }

    public void showSortedOrdersByDateOfSubmission(boolean flag) {
        orderService.sortOrdersByDateOfSubmission(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    public void showSortedOrdersByDateOfCompletion(boolean flag) {
        orderService.sortOrdersByDateOfCompletion(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    public void showSortedOrdersByPrice(boolean flag) {
        orderService.sortOrdersByPrice(flag);
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission(true);
    }

    public void showOrdersOverPeriodOfTime(int fromYear, int fromMonth, int fromDay, int toYear,
                                           int toMonth, int toDay, SortType sortType, boolean flag) {
        orderService.findOrdersOverPeriodOfTime(fromYear, fromMonth, fromDay, toYear, toMonth, toDay, sortType, flag);
    }

    public void showNearestAvailableSlot() {
        orderService.showNearestAvailableDate();
    }

}
