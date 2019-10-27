package Printing;

import java.util.concurrent.locks.Condition;

class Printer {
    private final Condition condition;
    private boolean free;
    private int id;

    Printer(int id, Condition condition) {
        free = true;
        this.condition = condition;
        this.id = id;
    }

    void print(String message) {
        try {
            Thread.sleep(500);
            System.out.println("Printer " + id + ": " + message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void await() throws InterruptedException {
        condition.await();
    }

    void signal() {
        condition.signal();
    }

    boolean free() {
        return free;
    }

    void occupy() {
        free = false;
    }

    void leave() {
        free = true;
    }
}
