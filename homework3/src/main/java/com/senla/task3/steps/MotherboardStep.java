package com.senla.task3.steps;

import com.senla.task3.inter.ILineStep;
import com.senla.task3.inter.IProductPart;
import com.senla.task3.models.Motherboard;

public class MotherboardStep implements ILineStep {

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Установка материнской платы");
        return new Motherboard("Intel i5");
    }
}
