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

    private final OrderService orderService = new OrderService();
    private final MechanicService mechanicService = new MechanicService();
    private final GaragePlaceService garagePlaceService = new GaragePlaceService();


    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, int hours, int minutes) {

        if (!mechanicService.getMechanicList().contains(mechanicService.findMechanicById(mechanicId)) || !garagePlaceService.getPlaceList().contains(garagePlaceService.findPlaceByNumber(placeNumber))) {
            System.out.println("Ошибка: указаны неверные айдишники механика или место в гараже");
            return;
        }

        Mechanic mechanic = mechanicService.findMechanicById(mechanicId);
        GaragePlace garagePlace = garagePlaceService.findPlaceByNumber(placeNumber);

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

    public void showMechanicByOrderId(int id) {
        for (Order order : orderService.getOrders()) {
            if (order.getIndex() == id) {
                System.out.println("Механик №" + order.getMechanic().getIndex() + " " +
                        order.getMechanic().getName() + " " +
                        order.getMechanic().getSurname() +
                        ". Лет опыта: " + order.getMechanic().getExperience() + " " +
                        (order.getMechanic().isBusy() ? "Механик не занят" : "Механик занят"));
            }
        }
    }



}
