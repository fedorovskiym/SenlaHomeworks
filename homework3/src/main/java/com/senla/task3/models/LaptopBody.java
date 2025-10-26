package com.senla.task3.models;

import com.senla.task3.inter.IProductPart;

public class LaptopBody implements IProductPart {

    private String material;

    public LaptopBody(String material) {
        this.material = material;
        System.out.println("Корпус ноутбука: " + material);
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

}
