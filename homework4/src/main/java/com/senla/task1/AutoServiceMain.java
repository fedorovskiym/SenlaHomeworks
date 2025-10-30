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
        autoService.addMechanic(new Mechanic("123", "123", 15));
        System.out.println();

        autoService.addGaragePlace(1);
        autoService.addGaragePlace(2);
        autoService.addGaragePlace(3);
        autoService.addGaragePlace(4);
        System.out.println();

        autoService.showFreeGaragePlaces();
        autoService.showAllMechanic();

        autoService.createOrder("BMW M5", 1, 1, 7000, 2, 30);
        autoService.acceptOrder(1);

        autoService.createOrder("BMW M2", 3, 3, 5000, 1, 0);

        autoService.acceptOrder(2);

//        System.out.println("Показ всех заказов");
//        autoService.showAllOrders();
//
//        System.out.println("Показ по дате выполнения");
//        autoService.showSortedOrdersByDateOfCompletion();
//
//        System.out.println("Показ по дате выполнения (перевернутый)");
//        autoService.showSortedOrdersByDateOfCompletionReversed();
//
//        System.out.println("Показ по дате подачи");
//        autoService.showSortedOrdersByDateOfSubmission();
//
//        System.out.println("Показ по дате подачи (перевернутый)");
//        autoService.showSortedOrdersByDateOfSubmissionReversed();

//        System.out.println("Показ по цене");
//        autoService.showSortedOrdersByPrice();
//
//        System.out.println("Показ по цене (перевернутый)");
//        autoService.showSortedOrdersByPriceReversed();

        System.out.println("Вывод свободных мест в гараже");
        autoService.showFreeGaragePlaces();

        System.out.println("Показ мастеров по алфавиту");
        autoService.showSortedMechanicByAlphabet();

        System.out.println("Показ мастеров по занятости");
        autoService.showSortedMechanicByBusy();


        autoService.getOrderByMechanicId(2);

    }
}
