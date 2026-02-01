package com.senla.task1.controller;

import com.senla.task1.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AutoServiceController {

    private final AutoService autoService;

    @Autowired
    public AutoServiceController(AutoService autoService) {
        this.autoService = autoService;
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
