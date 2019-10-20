class CountingSemaphore {
    private int counter;

    CountingSemaphore(int counter) {
        this.counter = counter;
    }

    synchronized void p() {
        while (counter == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        counter -= 1;
        notifyAll();
    }

    synchronized void v() {
        counter += 1;
        notifyAll();
    }
}
