package com.senla.task1.models;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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
    private Duration duration;
    private double price;

    public Order(String carName, Mechanic mechanic, GaragePlace garagePlace, Duration duration, double price) {
        this.index = count++;
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = "Ожидает";
        this.submissionDateTime = LocalDateTime.now();
        this.price = price;
        this.duration = duration;
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
        duration = duration.plus(time);
        endDateTime = endDateTime.plus(time);
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
