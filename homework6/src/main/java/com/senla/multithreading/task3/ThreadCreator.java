package com.senla.multithreading.task3;

import java.util.Random;

import static java.lang.Thread.sleep;

public class ThreadCreator implements Runnable {

    private final Buffer buffer;
    private final Random random = new Random();
    private final int count;

    public ThreadCreator(Buffer buffer, int count) {
        this.buffer = buffer;
        this.count = count;
    }

    @Override
    public void run() {
        try {
            for (int i = count; i > 0; i--) {
                int value = random.nextInt(1, 11);
                buffer.putValue(value);
                sleep(100);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
