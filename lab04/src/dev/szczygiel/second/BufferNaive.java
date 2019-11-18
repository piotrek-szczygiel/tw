package dev.szczygiel.second;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BufferNaive implements Buffer {
    private final int size;

    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    private int items = 0;

    BufferNaive(int size) {
        this.size = size;
    }

    @Override
    public void put(int n) {
        lock.lock();
        try {
            while (n + items > size) {
                condition.await();
            }

            items += n;
            condition.signalAll();
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void take(int n) {
        lock.lock();
        try {
            while (n > items) {
                condition.await();
            }

            items -= n;
            condition.signalAll();
        } catch (InterruptedException ignored) {
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        return size;
    }
}
