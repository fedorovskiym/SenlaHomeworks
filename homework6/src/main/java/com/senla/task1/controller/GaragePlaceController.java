package com.senla.task1.controller;

import com.senla.task1.models.GaragePlace;
import com.senla.task1.service.GaragePlaceService;

public class GaragePlaceController {

    private final GaragePlaceService garagePlaceService = GaragePlaceService.getInstance();

    public void showFreeGaragePlaces() {
        garagePlaceService.showFreeGaragePlaces();
    }

    public void addGaragePlace(int placeNumber) {
        garagePlaceService.addGaragePlace(placeNumber);
    }

    public void removeGaragePlace(int placeNumber) {
        garagePlaceService.removeGaragePlace(placeNumber);
    }

}
