package com.senla.task1.controller;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.service.AutoService;

public class AutoServiceController {

    private final AutoService autoService;

    @Inject
    public AutoServiceController(AutoService autoService) {
        this.autoService = autoService;
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Контроллер сервиса создался");
    }

    public void createOrder(String carModel, int mechanicId, int placeNumber, double price, int hours, int minutes) {
        autoService.createOrder(carModel, mechanicId, placeNumber, price, hours, minutes);
    }

    public void getAvailableSlot(int year, int month, int day) {
        autoService.getAvailableSlot(year, month, day);
    }

    public void exportOrdersToCSV(String filePath) {
        autoService.exportOrdersToCSV(filePath);
    }

    public void importOrdersFromCSV(String filePath) {
        autoService.importFromCSV(filePath);
    }

}
