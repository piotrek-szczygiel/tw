package dev.szczygiel;

class Counter {
    private int value;

    synchronized int getValue() {
        return value;
    }

    synchronized void increment() {
        this.value += 1;
    }

    synchronized void decrement() {
        this.value -= 1;
    }
}
