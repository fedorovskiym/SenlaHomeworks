package com.senla.task3;

import com.senla.task3.inter.IAssemblyLine;
import com.senla.task3.inter.ILineStep;
import com.senla.task3.inter.IProduct;
import com.senla.task3.inter.IProductPart;

public class LaptopAssemblyLine implements IAssemblyLine {

    private ILineStep laptopBodyStep;
    private ILineStep motherboardStep;
    private ILineStep screenStep;

    public LaptopAssemblyLine(ILineStep laptopBodyStep, ILineStep motherboardStep, ILineStep screenStep) {
        this.laptopBodyStep = laptopBodyStep;
        this.motherboardStep = motherboardStep;
        this.screenStep = screenStep;
    }

    @Override
    public IProduct assembleProduct(IProduct product) {

        IProductPart laptopBody = laptopBodyStep.buildProductPart();
        product.installFirstPart(laptopBody);

        IProductPart motherboard = motherboardStep.buildProductPart();
        product.installSecondPart(motherboard);

        IProductPart screen = screenStep.buildProductPart();
        product.installThirdPart(screen);

        System.out.println("Сборка завершена");

        return product;
    }
}
