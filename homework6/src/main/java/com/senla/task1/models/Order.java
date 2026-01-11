package com.senla.task1.models;

import com.senla.task1.models.enums.OrderStatus;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

public class Order implements Serializable {
    private static final long serialVersionUID = 123L;
    private static int count = 1;
    private Integer id;
    private String carName;
    private Mechanic mechanic;
    private GaragePlace garagePlace;
    private OrderStatus status;
    private LocalDateTime submissionDateTime;
    private LocalDateTime plannedCompletionDateTime;
    private LocalDateTime completionDateTime;
    private LocalDateTime endDateTime;
    private Duration duration;
    private double price;

    public Order() {
    }

    public Order(String carName, Mechanic mechanic, GaragePlace garagePlace, Duration duration, double price) {
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = OrderStatus.WAITING;
        this.submissionDateTime = LocalDateTime.now();
        this.price = price;
        this.duration = duration;
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
    }

    public Order(Integer id, String carName, Mechanic mechanic, GaragePlace garagePlace, OrderStatus status,
                 LocalDateTime submissionDateTime, LocalDateTime plannedCompletionDateTime,
                 LocalDateTime completionDateTime, LocalDateTime endDateTime,
                 Duration duration, double price) {
        this.id = id;
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = status;
        this.submissionDateTime = submissionDateTime;
        this.plannedCompletionDateTime = plannedCompletionDateTime;
        this.completionDateTime = completionDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.price = price;
    }

    public void acceptOrder() {
        status = OrderStatus.ACCEPTED;
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
    }

    public void closeOrder() {
        status = OrderStatus.DONE;
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public void cancelOrder() {
        status = OrderStatus.CANCEL;
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public Duration shiftTime(Duration time) {
        duration = duration.plus(time);
        endDateTime = endDateTime.plus(time);
        return time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus orderStatus) {
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

    @Override
    public String toString() {
        return "Order{" +
                "index=" + id +
                ", carName='" + carName + '\'' +
                ", mechanic=" + mechanic +
                ", garagePlace=" + garagePlace +
                ", status=" + status +
                ", submissionDateTime=" + submissionDateTime +
                ", plannedCompletionDateTime=" + plannedCompletionDateTime +
                ", completionDateTime=" + completionDateTime +
                ", endDateTime=" + endDateTime +
                ", duration=" + duration +
                ", price=" + price +
                '}';
    }
}
