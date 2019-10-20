class Counter {
    private BinarySemaphore sem;
    private int value = 0;

    Counter(BinarySemaphore sem) {
        this.sem = sem;
    }

    void inc() {
        sem.p();
        value += 1;
        sem.v();
    }

    void dec() {
        sem.p();
        value -= 1;
        sem.v();
    }

    int getValue() {
        sem.p();
        int v = value;
        sem.v();

        return v;
    }
}
