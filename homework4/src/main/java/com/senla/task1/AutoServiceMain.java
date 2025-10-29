package com.senla.task1;

import com.senla.task1.models.Mechanic;
import com.senla.task1.service.AutoService;

public class AutoServiceMain {
    public static void main(String[] args) {

        AutoService autoService = new AutoService();

        autoService.addMechanic(new Mechanic("Иван", "Иванов", 7));
        autoService.addMechanic(new Mechanic("Петр", "Петров", 3));
        autoService.addMechanic(new Mechanic("Сергей", "Сергеев", 10));

        autoService.addGaragePlace(1);
        autoService.addGaragePlace(2);
        autoService.addGaragePlace(3);

        autoService.showAllGaragePlaces();
        autoService.showAllMechanic();

        autoService.createOrder("BMW M5", 1, 1, 5);
        autoService.createOrder("Kia K5", 2, 2, 2);
        autoService.createOrder("Toyota  Camry", 3, 3, 1);

        autoService.showAllOrders();

        autoService.shiftOrdersTime(1);
        autoService.showAllOrders();

        autoService.closeOrder(1);

        autoService.cancelOrder(2);

        autoService.deleteOrder(3);

        autoService.showAllOrders();

        autoService.removeMechanic(1);
        autoService.removeGaragePlace(2);

        autoService.showAllGaragePlaces();
        autoService.showAllMechanic();

        autoService.createOrder("Toyota  Camry", 3, 3, 1);

    }
}
