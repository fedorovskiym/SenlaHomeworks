package com.senla.multithreading.task1;

import static java.lang.Thread.sleep;

public class MyThread implements Runnable {

    private static Object object = new Object();

    public static Object getObject() {
        return object;
    }

    @Override
    public void run() {
        try {
            synchronized (object) {
                object.wait();
            }
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
