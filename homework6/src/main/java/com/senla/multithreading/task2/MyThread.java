package com.senla.multithreading.task2;

import static java.lang.Thread.sleep;

public class MyThread implements Runnable{

    private Object lock;

    public MyThread(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (lock) {
                try {
                    sleep(500);
                    System.out.println(Thread.currentThread().getName());
                    lock.notify();
                    lock.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
