package com.senla.task2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Bouquet {

    private final List<Flower> flowerList = new ArrayList<>();

    public void addFlower(Flower flower) {
        flowerList.add(flower);
        System.out.println(flower.getName() + " добавлен в количестве " + flower.getCount() + " (" + flower.getPrice() * flower.getCount() + " руб.)");
    }

    public void showBouquet() {
        System.out.println("Букет: ");
        for (Flower flower : flowerList) {
            System.out.println(flower.getName() + " " + flower.getCount() + " шт. (" + flower.getPrice() * flower.getCount() + " руб.)");
        }
        System.out.println("Общая стоимость: " + calculatePrice() + " руб.");
    }

    public double calculatePrice() {
        double sum = 0;
        for (Flower flower : flowerList) {
            sum += flower.getCount() * flower.getPrice();
        }
        return sum;
    }

}
