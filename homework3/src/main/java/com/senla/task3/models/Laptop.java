package com.senla.task3.models;

import com.senla.task3.inter.IProduct;
import com.senla.task3.inter.IProductPart;

public class Laptop implements IProduct {

    private IProductPart laptopBody;
    private IProductPart motherboard;
    private IProductPart screen;

    @Override
    public void installFirstPart(IProductPart part) {
        this.laptopBody = part;
        System.out.println("Корпус установлен");
    }

    @Override
    public void installSecondPart(IProductPart part) {
        this.motherboard = part;
        System.out.println("Материнская плата установлена");
    }

    @Override
    public void installThirdPart(IProductPart part) {
        this.screen = part;
        System.out.println("Экран ноутбука установлен");
    }
}
