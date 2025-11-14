package br.ufrn.imd.imd_travel.service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

public class BufferCacheService {

    private final int capacity;
    private final Deque<Double> buffer;
    private final ReentrantLock lock = new ReentrantLock();

    private double sum = 0.0;

    public BufferCacheService(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacidade deve ser positiva");
        }
        this.capacity = capacity;
        this.buffer = new ArrayDeque<>(capacity);
    }

    public void add(double value) {
        lock.lock();
        try {
            if (buffer.size() == capacity) {
                double removed = buffer.removeFirst();
                sum -= removed;
            }

            buffer.addLast(value);
            sum += value;
        } finally {
            lock.unlock();
        }
    }

    public double getAverage() {
        lock.lock();
        try {
            if (buffer.isEmpty()) {
                return 0.0;
            }
            return sum / buffer.size();
        } finally {
            lock.unlock();
        }
    }
}
