package com.senla.task1.models;

import java.time.LocalDateTime;

public class Order {

    private static int count = 1;
    private int index;
    private String carName;
    private Mechanic mechanic;
    private GaragePlace garagePlace;
    private String status;
    private double durationHours;

    public Order(String carName, Mechanic mechanic, GaragePlace garagePlace, double durationHours) {
        this.index = count++;
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = "Принят";
        this.durationHours = durationHours;
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
    }

    public void closeOrder() {
        status = "Выполнен";
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public void cancelOrder() {
        status = "Отменен";
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public void deleteOrder() {
        status = "Удален";
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public void shiftTime(double hours) {
        durationHours += hours;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    public GaragePlace getGaragePlace() {
        return garagePlace;
    }

    public void setGaragePlace(GaragePlace garagePlace) {
        this.garagePlace = garagePlace;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(double durationHours) {
        this.durationHours = durationHours;
    }

}
