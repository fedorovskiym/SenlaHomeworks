package com.senla.task2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bouquet {


    private final HashMap<Flower, Integer> flowers = new HashMap<>();

    public void addFlower(Flower flower, Integer count) {
        flowers.put(flower, count);
        System.out.println(flower.getName() + " добавлен в букет в количестве " + count + " шт. Цена - " + flower.getPrice() * count + " руб.");
    }

    public void showBouquet() {
        System.out.println("Букет: ");
        for (Map.Entry<Flower, Integer> entry : flowers.entrySet()){
            System.out.println(entry.getKey().getName() + " " + entry.getValue() + " шт.");
        }
        System.out.println("Общая стоимость букета: " + calculatePrice() + " руб.");
    }

    public double calculatePrice() {
        double sum = 0;
        for (Map.Entry<Flower, Integer> entry : flowers.entrySet()){
            sum += entry.getKey().getPrice() * entry.getValue();
        }
        return sum;
    }

}
