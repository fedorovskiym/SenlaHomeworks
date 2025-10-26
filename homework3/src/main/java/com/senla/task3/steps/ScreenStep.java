package com.senla.task3.steps;

import com.senla.task3.inter.ILineStep;
import com.senla.task3.inter.IProductPart;
import com.senla.task3.models.Screen;

public class ScreenStep implements ILineStep {

    @Override
    public IProductPart buildProductPart() {
        System.out.println("Установка экрана");
        return new Screen(15.6);
    }
}
