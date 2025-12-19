package com.senla.multithreading.task3;

public class Task3 {
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5);
        Thread threadCreator = new Thread(new ThreadCreator(buffer, 15));
        Thread threadConsumer = new Thread(new ThreadConsumer(buffer));
        threadCreator.start();
        threadConsumer.start();
    }
}
