package com.senla.task1.models;

public class GaragePlace {

    private int placeNumber;
    private boolean isEmpty;

    public GaragePlace(int placeNumber) {
        this.placeNumber = placeNumber;
        this.isEmpty = true;
    }

    public int getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(int placeNumber) {
        this.placeNumber = placeNumber;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }
}
