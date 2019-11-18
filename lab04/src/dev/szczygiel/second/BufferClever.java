package dev.szczygiel.second;

import java.util.concurrent.Semaphore;

public class BufferClever implements Buffer {
    private final int size;
    private final Semaphore semaphore;

    BufferClever(int size) {
        this.size = size;
        semaphore = new Semaphore(size, true);
    }

    @Override
    public void put(int n) {
        semaphore.release(n);
    }

    @Override
    public void take(int n) {
        try {
            semaphore.acquire(n);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public int size() {
        return size;
    }
}
