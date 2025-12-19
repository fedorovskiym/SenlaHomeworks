package com.senla.multithreading.task4;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

public class MyThread implements Runnable{

    private long n;

    public MyThread(long n) {
        this.n = n;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Текущее время: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            try {
                sleep(n * 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
