package dev.szczygiel.second;

import java.util.concurrent.Semaphore;

public class BufferClever implements Buffer {
    private final int size;
    private final Semaphore semaphore;
    private final Semaphore semaphore2;

    BufferClever(int size) {
        this.size = size;
        semaphore = new Semaphore(0, true);
        semaphore2 = new Semaphore(size, true);
    }

    @Override
    public void put(int n) {
        try {
            semaphore2.acquire(n);
            semaphore.release(n);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void take(int n) {
        try {
            semaphore.acquire(n);
            semaphore2.release(n);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public int size() {
        return size;
    }
}
