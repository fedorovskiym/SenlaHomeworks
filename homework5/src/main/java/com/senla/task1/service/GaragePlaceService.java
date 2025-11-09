package com.senla.task1.service;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.models.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GaragePlaceService {

    private final List<GaragePlace> placeList = new ArrayList<>();

    public List<GaragePlace> getPlaceList() {
        return placeList;
    }

    public GaragePlace findPlaceByNumber(int placeNumber) {
        for (GaragePlace garagePlace : placeList) {
            if (garagePlace.getPlaceNumber() == placeNumber) {
                return garagePlace;
            }
        }
        return null;
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

    // Проверка свободно ли место в гараже на дату
    public boolean isGaragePlaceAvailable(GaragePlace garagePlace, List<Order> orders, LocalDateTime startDate, LocalDateTime endDate) {
        for (Order order : orders) {
            if (order.getStatus().equals("Отменен") || order.getStatus().equals("Удален")) continue;

            if (order.getGaragePlace().equals(garagePlace)) {
                LocalDateTime start = order.getSubmissionDateTime();
                LocalDateTime end = order.getPlannedCompletionDateTime();

                if (start == null || end == null) continue;

                if (!end.isBefore(startDate) && !start.isAfter(endDate)) {
                    return false;
                }
            }
        }
        return true;
    }
}
