class BinarySemaphore {
    private boolean open = true;

    synchronized void p() {
        while (!open) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        open = false;
        notifyAll();
    }

    synchronized void v() {
        open = true;
        notifyAll();
    }
}
