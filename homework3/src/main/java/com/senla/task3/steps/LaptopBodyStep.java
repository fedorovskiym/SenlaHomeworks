package com.senla.task3.steps;

import com.senla.task3.inter.ILineStep;
import com.senla.task3.inter.IProductPart;
import com.senla.task3.models.LaptopBody;

public class LaptopBodyStep implements ILineStep {

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Установка корпуса");
        return new LaptopBody("алюминий");
    }
}
