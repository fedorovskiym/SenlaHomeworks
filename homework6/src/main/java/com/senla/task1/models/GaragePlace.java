package com.senla.task1.models;

import java.io.Serializable;

public class GaragePlace implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer id;
    private int placeNumber;
    private boolean isEmpty;

    public GaragePlace() {
    }

    public GaragePlace(int placeNumber) {
        this.placeNumber = placeNumber;
        this.isEmpty = true;
    }

    public GaragePlace(Integer id, int placeNumber, boolean isEmpty) {
        this.id = id;
        this.placeNumber = placeNumber;
        this.isEmpty = isEmpty;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GaragePlace{" +
                "placeNumber=" + placeNumber +
                ", isEmpty=" + isEmpty +
                '}';
    }
}
