package dev.szczygiel.first;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Pipeline {
    private int[] buffer;
    private Lock[] locks;
    private Condition[] conditions;

    Pipeline(int bufferSize) {
        buffer = new int[bufferSize];
        locks = new Lock[bufferSize];
        conditions = new Condition[bufferSize];

        for (int i = 0; i < bufferSize; ++i) {
            buffer[i] = -1;
            locks[i] = new ReentrantLock();
            conditions[i] = locks[i].newCondition();
        }
    }

    int[] getBuffer() {
        return buffer;
    }

    Condition lock(int i) {
        locks[i].lock();
        return conditions[i];
    }

    void unlock(int i) {
        locks[i].unlock();
    }
}
