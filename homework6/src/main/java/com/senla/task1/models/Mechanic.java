package com.senla.task1.models;

import java.io.Serializable;

public class Mechanic implements Serializable {
    private static final long serialVersionUID = 12L;
    private Integer id;
    private String name;
    private String surname;
    private double experienceYears;
    private boolean isBusy;

    public Mechanic() {
    }

    public Mechanic(String name, String surname, double experienceYears) {
        this.name = name;
        this.surname = surname;
        this.experienceYears = experienceYears;
        this.isBusy = false;
    }

    public Mechanic(Integer id, String name, String surname, double experienceYears, boolean isBusy) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.experienceYears = experienceYears;
        this.isBusy = isBusy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public double getExperience() {
        return experienceYears;
    }

    public void setExperience(double experienceYears) {
        this.experienceYears = experienceYears;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    @Override
    public String toString() {
        return "Mechanic{" +
                "index=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", experienceYears=" + experienceYears +
                ", isBusy=" + isBusy +
                '}';
    }
}
