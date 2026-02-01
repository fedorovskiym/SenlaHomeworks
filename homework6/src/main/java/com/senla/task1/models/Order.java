package com.senla.task1.models;

import com.senla.task1.util.DurationConverter;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order implements Serializable {
    private static final long serialVersionUID = 123L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "car_name")
    private String carName;
    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;
    @ManyToOne
    @JoinColumn(name = "garage_place_id")
    private GaragePlace garagePlace;
    @ManyToOne
    @JoinColumn(name = "order_status_id")
    private OrderStatus status;
    @Column(name = "submission_date_time")
    private LocalDateTime submissionDateTime;
    @Column(name = "planned_completion_date_time")
    private LocalDateTime plannedCompletionDateTime;
    @Column(name = "completion_date_time")
    private LocalDateTime completionDateTime;
    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;
    @Column(name = "duration")
    @Convert(converter = DurationConverter.class)
    private Duration duration;
    @Column(name = "price")
    private Double price;

    public Order() {
    }

    public Order(String carName, Mechanic mechanic, GaragePlace garagePlace, Duration duration, Double price, OrderStatus orderStatus) {
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = orderStatus;
        this.submissionDateTime = LocalDateTime.now();
        this.price = price;
        this.duration = duration;
    }

    public Order(Integer id, String carName, Mechanic mechanic, GaragePlace garagePlace, OrderStatus orderStatus,
                 LocalDateTime submissionDateTime, LocalDateTime plannedCompletionDateTime,
                 LocalDateTime completionDateTime, LocalDateTime endDateTime,
                 Duration duration, Double price) {
        this.id = id;
        this.carName = carName;
        this.mechanic = mechanic;
        this.garagePlace = garagePlace;
        this.status = orderStatus;
        this.submissionDateTime = submissionDateTime;
        this.plannedCompletionDateTime = plannedCompletionDateTime;
        this.completionDateTime = completionDateTime;
        this.endDateTime = endDateTime;
        this.duration = duration;
        this.price = price;
    }

    public void acceptOrder() {
        mechanic.setBusy(true);
        garagePlace.setEmpty(false);
    }

    public void closeOrder() {
        mechanic.setBusy(false);
        garagePlace.setEmpty(true);
    }

    public void cancelOrder() {
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

    public void setStatus(OrderStatus status) {
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
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
