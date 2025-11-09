package com.senla.task1.controller;

import com.senla.task1.service.AutoService;

public class AutoServiceController {

    private final AutoService autoService = new AutoService();

    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, int hours, int minutes) {
        autoService.createOrder(carModel, mechanicId, placeNumber, price, hours, minutes);
    }

    public void getAvailableSlot(int year, int month, int day) {
        autoService.getAvailableSlot(year, month, day);
    }

}
