package com.senla.multithreading.task3;

import static java.lang.Thread.sleep;

public class ThreadConsumer implements Runnable {

    private final Buffer buffer;

    public ThreadConsumer(Buffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            int value = buffer.getValue();
            System.out.println("Забрал значение: " + value);
            try {
                sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

