package com.senla.task1.controller;

import com.senla.task1.service.GaragePlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class GaragePlaceController {

    private final GaragePlaceService garagePlaceService;

    @Autowired
    public GaragePlaceController(GaragePlaceService garagePlaceService) {
        this.garagePlaceService = garagePlaceService;
    }

    public void showFreeGaragePlaces() {
        garagePlaceService.findFreeGaragePlaces();
    }

    public void addGaragePlace(int placeNumber) {
        garagePlaceService.addGaragePlace(placeNumber);
    }

    public void removeGaragePlace(int placeNumber) {
        garagePlaceService.removeGaragePlace(placeNumber);
    }

    public void importGaragePlaceFromCSV(String filePath) {
        garagePlaceService.importFromCSV(filePath);
    }

    public void exportGaragePlaceToCSV(String filePath) {
        garagePlaceService.exportToCSV(filePath);
    }
}
