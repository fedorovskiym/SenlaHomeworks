package com.senla.task1.exceptions;

public class ExceptionHandler {
    public static void handle(Exception e) {
        if (e instanceof NumberFormatException) {
            System.out.println("Ошибка ввода. Ожидалось число");
        } else if (e instanceof InvalidInputException) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        } else {
            System.out.println("Неизвестная ошибка: " + e.getMessage());
        }
    }
}
