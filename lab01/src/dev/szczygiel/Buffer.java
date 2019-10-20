package dev.szczygiel;

class Buffer {
    private String buffer;
    private boolean empty = true;

    synchronized void put(String value) {
        while (!this.empty) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        this.empty = false;

        this.buffer = value;
        notifyAll();
    }

    synchronized String take() {
        while (this.empty) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }

        this.empty = true;

        notifyAll();
        return this.buffer;
    }
}