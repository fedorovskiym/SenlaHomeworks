package com.senla.task1.models;

import java.io.Serializable;

public class Mechanic implements Serializable {
    private static final long serialVersionUID = 12L;
    private static int count = 1;
    private int index;
    private String name;
    private String surname;
    private double experienceYears;
    private boolean isBusy;

    public Mechanic(String name, String surname, double experienceYears) {
        this.index = count++;
        this.name = name;
        this.surname = surname;
        this.experienceYears = experienceYears;
        this.isBusy = false;
    }

    public Mechanic(int index, String name, String surname, double experienceYears, boolean isBusy) {
        this.index = index;
        this.name = name;
        this.surname = surname;
        this.experienceYears = experienceYears;
        this.isBusy = isBusy;
        if(index > count) {
            index++;
            count = index;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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
                "index=" + index +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", experienceYears=" + experienceYears +
                ", isBusy=" + isBusy +
                '}';
    }
}
