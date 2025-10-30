package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Mechanic;
import com.senla.task1.models.Order;
import com.senla.task1.service.OrderService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AutoService {

    private final List<Mechanic> mechanicList = new ArrayList<>();
    private final List<GaragePlace> placeList = new ArrayList<>();
    private final OrderService orderService = new OrderService();

    public void addMechanic(Mechanic mechanic) {
        mechanicList.add(mechanic);
        System.out.println("Добавлен механик " + mechanic.getName() + " " + mechanic.getSurname() + ". Опыт: " + mechanic.getExperience() + " лет/год(а/ов)");
    }

    public void removeMechanic(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                mechanicList.remove(mechanic);
                System.out.println("Удален механик " + mechanic.getName());
                return;
            }
        }
    }

    public Object findMechanicById(int id) {
        for (Mechanic mechanic : mechanicList) {
            if (mechanic.getIndex() == id) {
                return mechanic;
            }
        }
        return null;
    }

    public Object findPlaceByNumber(int placeNumber) {
        for (GaragePlace garagePlace : placeList) {
            if (garagePlace.getPlaceNumber() == placeNumber) {
                return garagePlace;
            }
        }
        return null;
    }

    public void showAllMechanic() {
        for (Mechanic mechanic : mechanicList) {
            System.out.println("Механик №" + mechanic.getIndex() + " " +
                    mechanic.getName() + " " +
                    mechanic.getSurname() +
                    ". Лет опыта: " + mechanic.getExperience() + " " +
                    (mechanic.isBusy() ? "Механик не занят" : "Механик занят"));
        }
        System.out.println();
    }

    public void showFreeGaragePlaces() {
        for (GaragePlace garagePlace : placeList) {
            if (garagePlace.isEmpty()) {
                System.out.println("Место №" + garagePlace.getPlaceNumber() + " свободно");
            }
        }
        System.out.println();
    }

    public void addGaragePlace(int number) {
        placeList.add(new GaragePlace(number));
        System.out.println("Добавлено место в гараж №" + number);
    }

    public void removeGaragePlace(int number) {
        for (GaragePlace place : placeList) {
            if (place.getPlaceNumber() == number) {
                placeList.remove(place);
                System.out.println("Место в гараже №" + number + " удалено");
                return;
            }
        }
    }

    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, long hours, long minutes) {

        if (!mechanicList.contains(findMechanicById(mechanicId)) || !placeList.contains(findPlaceByNumber(placeNumber))) {
            System.out.println("Ошибка: указаны неверные айдишники механика или место в гараже");
            return;
        }

        Mechanic mechanic = (Mechanic) findMechanicById(mechanicId);
        GaragePlace garagePlace = (GaragePlace) findPlaceByNumber(placeNumber);

        if (mechanic.isBusy() || !garagePlace.isEmpty()) {
            System.out.println("Механик или место занято!");
            return;
        }

        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        Order order = new Order(carModel, mechanic, garagePlace, duration, price);

        orderService.addOrder(order);
    }

    public void getOrderByMechanicId(int mechanicId) {
        orderService.findOrderByMechanicId(mechanicId);
    }

    public void acceptOrder(int id) {
        orderService.acceptOrder(id);
    }

    public void deleteOrder(int id) {
        orderService.deleteOrder(id);
    }

    public void closeOrder(int id) {
        orderService.closeOrder(id);
    }

    public void cancelOrder(int id) {
        orderService.cancelOrder(id);
    }

    public void shiftOrdersTime(int hours, int minutes) {
        Duration duration = Duration.ofHours(hours).plusMinutes(minutes);
        orderService.shiftOrders(duration);
    }

    public void showOrderByStatus(String status) {
        orderService.showOrders(status);
    }

    public void showAllOrders() {
        orderService.showOrders();
    }

    public void showSortedOrdersByDateOfSubmission() {
        orderService.sortOrdersByDateOfSubmission();
        orderService.showOrders();
    }

    public void showSortedOrdersByDateOfSubmissionReversed() {
        orderService.sortOrdersByDateOfSubmissionReversed();
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission();
    }

    public void showSortedOrdersByDateOfCompletion() {
        orderService.sortOrdersByDateOfCompletion();
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission();
    }

    public void showSortedOrdersByDateOfCompletionReversed() {
        orderService.sortOrdersByDateOfCompletionReversed();
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission();
    }

    public void showSortedOrdersByPrice() {
        orderService.sortOrdersByPrice();
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission();
    }

    public void showSortedOrdersByPriceReversed() {
        orderService.sortOrdersByPriceReversed();
        orderService.showOrders();
        orderService.sortOrdersByDateOfSubmission();
    }

    public void showSortedMechanicByAlphabet() {
        mechanicList.sort(Comparator.comparing(Mechanic::getName));
        showAllMechanic();
    }

    public void showSortedMechanicByBusy() {
        mechanicList.sort(Comparator.comparing(Mechanic::isBusy));
        showAllMechanic();
    }


}
