package com.senla.task3;

import com.senla.task3.inter.IAssemblyLine;
import com.senla.task3.inter.ILineStep;
import com.senla.task3.inter.IProduct;
import com.senla.task3.models.Laptop;
import com.senla.task3.steps.LaptopBodyStep;
import com.senla.task3.steps.MotherboardStep;
import com.senla.task3.steps.ScreenStep;

public class Main {
    public static void main(String[] args) {

        ILineStep laptopBodyStep = new LaptopBodyStep();
        ILineStep motherboardStep = new MotherboardStep();
        ILineStep screenStep = new ScreenStep();

        IAssemblyLine assemblyLine = new LaptopAssemblyLine(laptopBodyStep, motherboardStep, screenStep);

        IProduct laptop = new Laptop();
        assemblyLine.assembleProduct(laptop);

        System.out.println(laptop);
    }
}
