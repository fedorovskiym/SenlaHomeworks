package com.senla.task3.models;

import com.senla.task3.inter.IProductPart;

public class Screen implements IProductPart {

    private double diagonal;

    public Screen(double diagonal) {
        this.diagonal = diagonal;
        System.out.println("Экран ноутбука: " + diagonal + " дюймов");
    }

    public double getDiagonal() {
        return diagonal;
    }

    public void setDiagonal(double diagonal) {
        this.diagonal = diagonal;
    }
}
