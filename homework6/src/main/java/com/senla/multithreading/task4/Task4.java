package com.senla.multithreading.task4;

public class Task4 {
    public static void main(String[] args) {
        Thread myThread = new Thread(new MyThread(5));
        myThread.start();
    }
}
