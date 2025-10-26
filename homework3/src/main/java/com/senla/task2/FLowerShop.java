package com.senla.task2;

public class FLowerShop {
    public static void main(String[] args) {
        Bouquet bouquet = new Bouquet();

        bouquet.addFlower(new Rose(120), 3);
        bouquet.addFlower(new Lily(150), 3);
        bouquet.addFlower(new Tulip(150), 3);
        System.out.println();

        bouquet.showBouquet();
    }
}
