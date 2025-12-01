package com.senla.task1.models;

import java.io.Serializable;

public class GaragePlace implements Serializable {

    private static final long serialVersionUID = 1L;
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

    @Override
    public String toString() {
        return "GaragePlace{" +
                "placeNumber=" + placeNumber +
                ", isEmpty=" + isEmpty +
                '}';
    }
}
