package com.senla.task1.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Order {

    private static int count = 1;
    private int index;
    private String carName;
    private Mechanic mechanic;
    private GaragePlace garagePlace;
    private String status;
    private LocalDateTime submissionDateTime;
    private LocalDateTime plannedCompletionDateTime;
    private LocalDateTime completionDateTime;
    private LocalDateTime endDateTime;
    private Duration durationHours;
    private double price;

    public Order(String carName, Mechanic mechanic, GaragePlace garagePlace, Duration durationHours, double price) {
        this.index = count++;
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = "Ожидает";
        this.submissionDateTime = LocalDateTime.now();
        this.price = price;
        this.durationHours = durationHours;
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
    }

    public void acceptOrder() {
        status = "Принят";
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

    public void shiftTime(Duration time) {
        durationHours = durationHours.plus(time);
        endDateTime = endDateTime.plus(time);
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Order.count = count;
    }

    public LocalDateTime getSubmissionDateTime() {
        return submissionDateTime;
    }

    public void setSubmissionDateTime(LocalDateTime submissionDateTime) {
        this.submissionDateTime = submissionDateTime;
    }

    public LocalDateTime getPlannedCompletionDateTime() {
        return plannedCompletionDateTime;
    }

    public void setPlannedCompletionDateTime(LocalDateTime plannedCompletionDateTime) {
        this.plannedCompletionDateTime = plannedCompletionDateTime;
    }

    public LocalDateTime getCompletionDateTime() {
        return completionDateTime;
    }

    public void setCompletionDateTime(LocalDateTime completionDateTime) {
        this.completionDateTime = completionDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setDurationHours(Duration durationHours) {
        this.durationHours = durationHours;
    }

    public Duration getDurationHours() {
        return durationHours;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
