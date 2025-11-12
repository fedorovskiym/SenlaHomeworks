package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.service.OrderService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoService {

    private OrderService orderService = OrderService.getInstance();
    private MechanicService mechanicService = MechanicService.getInstance();
    private GaragePlaceService garagePlaceService = GaragePlaceService.getInstance();


    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, int hours, int minutes) {

        Mechanic mechanic = mechanicService.findMechanicById(mechanicId);
        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(placeNumber);

        if (mechanic == null) {
            System.out.println("Ошибка: указан неверный номер механика");
            return;
        }

        if (garagePlace == null) {
            System.out.println("Ошибка: указан неверный номер гаража");
        }

        if (mechanic.isBusy() || !garagePlace.isEmpty()) {
            System.out.println("Механик или место занято!");
            return;
        }

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        Order order = new Order(carModel, mechanic, garagePlace, duration, price);

        orderService.addOrder(order);
    }

    public void getAvailableSlot(int year, int month, int day) {

        int availableMechanics = 0;
        int availableGaragePlaces = 0;

        List<Order> orders = orderService.getOrders();
        LocalDateTime startDate = LocalDateTime.of(year, month, day, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, day, 23, 59);

        for (Mechanic mechanic : mechanicService.getMechanicList()) {
            if (mechanicService.isMechanicAvailable(mechanic, orders, startDate, endDate)) {
                availableMechanics++;
            }
        }

        for (GaragePlace garagePlace : garagePlaceService.getPlaceList()) {
            if (garagePlaceService.isGaragePlaceAvailable(garagePlace, orders, startDate, endDate)) {
                availableGaragePlaces++;
            }
        }

        System.out.println("Количество свободных мест в сервисе на " + day + "." + month + "." + year + " - " + Math.min(availableMechanics, availableGaragePlaces));
    }

}
