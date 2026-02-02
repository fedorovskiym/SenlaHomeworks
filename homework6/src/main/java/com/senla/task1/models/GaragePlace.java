package com.senla.task1.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "garage_place")
public class GaragePlace implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "place_number")
    private Integer placeNumber;
    @Column(name = "is_empty")
    private Boolean isEmpty;

    public GaragePlace() {
    }

    public GaragePlace(Integer placeNumber) {
        this.placeNumber = placeNumber;
        this.isEmpty = true;
    }

    public GaragePlace(Integer id, Integer placeNumber, Boolean isEmpty) {
        this.id = id;
        this.placeNumber = placeNumber;
        this.isEmpty = isEmpty;
    }

    public Integer getPlaceNumber() {
        return placeNumber;
    }

    public void setPlaceNumber(Integer placeNumber) {
        this.placeNumber = placeNumber;
    }

    public Boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(Boolean empty) {
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
