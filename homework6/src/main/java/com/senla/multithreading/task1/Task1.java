package com.senla.multithreading.task1;

import static java.lang.Thread.sleep;

public class Task1 {
    public static void main(String[] args) {
        try {
            Thread thread1 = new Thread(new MyThread());
            System.out.println(thread1.getState());
            thread1.start();
            System.out.println(thread1.getState());
            Object object = MyThread.getObject();
            synchronized (object) {
                object.notify();
                sleep(500);
                System.out.println(thread1.getState());
            }
            sleep(200);
            System.out.println(thread1.getState());
            synchronized (object) {
                object.notify();
            }
            sleep(100);
            System.out.println(thread1.getState());
            thread1.join();
            System.out.println(thread1.getState());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
