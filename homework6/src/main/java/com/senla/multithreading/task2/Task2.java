package com.senla.multithreading.task2;

public class Task2 {
    public static void main(String[] args) {
        Object lock = new Object();
        Thread thread1 = new Thread(new MyThread(lock));
        Thread thread2 = new Thread(new MyThread(lock));
        thread1.start();
        thread2.start();

    }
}
