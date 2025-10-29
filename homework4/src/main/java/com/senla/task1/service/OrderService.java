package com.senla.task1.service;

import com.senla.task1.models.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private List<Order> orders = new ArrayList<>();

    public void addOrder(Order order) {
        orders.add(order);
        System.out.println("Заказ: " + order.getCarName() + " добавлен в гараж №" + order.getGaragePlace().getPlaceNumber() + ". Назначен механик " + order.getMechanic().getName());
        System.out.println();
    }

    public void closeOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.closeOrder();
                System.out.println("Заказ №" + id + " закрыт\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }

    public void cancelOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                order.cancelOrder();
                System.out.println("Заказ №" + id + " отменен\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }

    public void shiftOrders(double hours) {
        for (Order order : orders) {
            order.shiftTime(hours);
        }
        System.out.println("Сдвинуто время выполнения всех заказов изменено на " + hours + " час(а/ов)\n");
    }

    public void showOrders() {
        System.out.println("Заказы:");
        for (Order order : orders) {
            System.out.println("Заказ №" + order.getIndex() + ":\n" +
                    "Статус: " + order.getStatus() + "\n" +
                    "Механик: " + order.getMechanic().getName() + " " + order.getMechanic().getSurname() + "\n" +
                    "Название машины: " + order.getCarName() + "\n" +
                    "Место в гараже: " + order.getGaragePlace().getPlaceNumber() + "\n" +
                    "Длительность (в часах): " + order.getDurationHours() + "\n");
        }
        System.out.println();
    }

    public void deleteOrder(int id) {
        for (Order order : orders) {
            if (order.getIndex() == id) {
                orders.remove(order);
                order.deleteOrder();
                System.out.println("Заказ №" + id + " удален\n");
                return;
            }
        }
        System.out.println("Заказ №" + id + " не найден\n");
    }
}
