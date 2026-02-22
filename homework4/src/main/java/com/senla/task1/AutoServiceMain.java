package com.senla.task1;

import com.senla.task1.models.Mechanic;
import com.senla.task1.service.AutoService;

import java.time.Duration;
import java.time.LocalDateTime;

public class AutoServiceMain {
    public static void main(String[] args) {
        AutoService autoService = new AutoService();

        autoService.addMechanic(new Mechanic("Сергей", "Иванов", 7));
        autoService.addMechanic(new Mechanic("Петр", "Петров", 3));
        autoService.addMechanic(new Mechanic("Иван", "Сергеев", 10));
        System.out.println();

        autoService.addGaragePlace(1);
        autoService.addGaragePlace(2);
        autoService.addGaragePlace(3);
        System.out.println();

        autoService.showFreeGaragePlaces();
        autoService.showAllMechanic();

        autoService.createOrder("BMW M5", 1, 1, 7000, 2, 30);

        autoService.createOrder("Toyota Camry", 3, 3, 5000, 1, 0);

        autoService.showOrderByStatus("Принят");

        autoService.createOrder("Kia Rio", 2, 2, 10000, 3, 0);

        autoService.closeOrder(1);
        autoService.cancelOrder(3);

        autoService.shiftOrdersTime(1, 0);

        autoService.showAllOrders();

        autoService.showFreeGaragePlaces();

        autoService.showOrdersOverPeriodOfTime(2025, 11, 2, 2025, 11, 2, "completion", false);

        System.out.println("Показ по дате выполнения");
        autoService.showSortedOrdersByDateOfCompletion(true);

        System.out.println("Показ по дате выполнения (перевернутый)");
        autoService.showSortedOrdersByDateOfCompletion(false);

        System.out.println("Показ по дате подачи");
        autoService.showSortedOrdersByDateOfSubmission(true);

        System.out.println("Показ по дате подачи (перевернутый)");
        autoService.showSortedOrdersByDateOfSubmission(false);

        System.out.println("Показ по цене");
        autoService.showSortedOrdersByPrice(true);

        System.out.println("Показ по цене (перевернутый)");
        autoService.showSortedOrdersByPrice(false);

        System.out.println("Показ мастеров по алфавиту");
        autoService.showSortedMechanicByAlphabet(true);

        System.out.println("Показ мастеров по занятости");
        autoService.showSortedMechanicByBusy();

        autoService.getOrderByMechanicId(2);

        autoService.showNearestAvailableDate();

        autoService.getAvailableSlot(2025, 11, 2);

        autoService.getMechanicByOrderId(2);

    }
}
