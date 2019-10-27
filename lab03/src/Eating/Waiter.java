package Eating;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Waiter {
    private final Lock lock = new ReentrantLock();
    private List<Integer> peopleCounter = new ArrayList<>();
    private Table table;

    private Condition tableCondition;
    private List<Condition> pairConditions = new ArrayList<>();

    Waiter(int pairsNumber) {
        table = new Table();
        tableCondition = lock.newCondition();

        for (int i = 0; i < pairsNumber; ++i) {
            peopleCounter.add(0);
            pairConditions.add(lock.newCondition());
        }
    }

    Table getTable(int pairNumber) throws InterruptedException {
        lock.lock();

        peopleCounter.set(pairNumber, peopleCounter.get(pairNumber) + 1);

        if (peopleCounter.get(pairNumber) == 2) {
            pairConditions.get(pairNumber).signal();
        }

        while (peopleCounter.get(pairNumber) != 2) {
            pairConditions.get(pairNumber).await();
        }

        System.out.println("Pair " + pairNumber + " is waiting for the table");

        while (table.getPair() != -1 && table.getPair() != pairNumber) {
            tableCondition.await();
        }

        table.setPair(pairNumber);
        tableCondition.signalAll();
        lock.unlock();

        return table;
    }

    void unlockTable() {
        lock.lock();
        int pair = table.getPair();

        peopleCounter.set(pair, peopleCounter.get(pair) - 1);

        if (peopleCounter.get(pair) == 0) {
            table.setPair(-1);
            tableCondition.signal();
        }

        lock.unlock();
    }
}
