package com.senla.task3.models;

import com.senla.task3.inter.IProductPart;

public class Motherboard implements IProductPart {

    private String CPUModel;

    public Motherboard(String CPUModel) {
        this.CPUModel = CPUModel;
        System.out.println("Материнская плата: " + CPUModel);
    }

    public String getCPUModel() {
        return CPUModel;
    }

    public void setCPUModel(String CPUModel) {
        this.CPUModel = CPUModel;
    }
}
