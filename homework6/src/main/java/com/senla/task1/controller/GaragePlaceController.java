package com.senla.task1.controller;

import com.senla.task1.annotations.Inject;
import com.senla.task1.annotations.PostConstruct;
import com.senla.task1.models.GaragePlace;
import com.senla.task1.service.GaragePlaceService;

public class GaragePlaceController {

    @Inject
    private GaragePlaceService garagePlaceService;

    @PostConstruct
    public void postConstruct() {
        System.out.println("Контроллер мест создался");
    }

    public void showFreeGaragePlaces() {
        garagePlaceService.showFreeGaragePlaces();
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
