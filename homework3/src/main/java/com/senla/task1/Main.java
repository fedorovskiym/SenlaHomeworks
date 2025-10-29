package com.senla.task1;

import java.util.Random;

//Variant 4
public class Main {
    public static void main(String[] args) {
        Random random = new Random();

        int randomNum = 100 + random.nextInt(999);
        int sum = 0;
        int temp = randomNum;

        while (temp > 0) {
            sum += temp % 10;
            temp /= 10;
        }

        System.out.println("Число: " + randomNum);
        System.out.println("Сумма его цифр: " + sum);

    }
}
