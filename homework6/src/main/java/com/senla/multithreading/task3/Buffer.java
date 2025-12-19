package com.senla.multithreading.task3;

import java.util.LinkedList;
import java.util.Queue;

public class Buffer {

    private Queue<Integer> queue = new LinkedList<>();
    private Integer size;

    public Buffer(Integer size) {
        this.size = size;
    }

    public synchronized void putValue(int value) {
        if(queue.size() == size) {
            try {
                System.out.println("Ожидание освобождения буффера");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        queue.add(value);
        System.out.println("Добавлено значение: " + value);
        notifyAll();
    }

    public synchronized int getValue() {
        if(queue.isEmpty()) {
            try {
                System.out.println("Ожидание поступления данных");
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        notifyAll();
        return queue.poll();
    }
}
