import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BoundedBuffer {
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    private final Object[] items;
    private int putptr, takeptr, count;

    BoundedBuffer(int bound) {
        items = new Object[bound];
    }

    void put(Object x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }

            items[putptr] = x;
            if (++putptr == items.length) {
                putptr = 0;
            }

            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    Object take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }

            Object x = items[takeptr];
            if (++takeptr == items.length) {
                takeptr = 0;
            }

            --count;
            notFull.signal();

            return x;
        } finally {
            lock.unlock();
        }
    }
}
 