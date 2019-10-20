package dev.szczygiel;

class Producer implements Runnable {
    private Buffer buffer;

    Producer(Buffer buffer) {
        this.buffer = buffer;
    }

    public void run() {
        for (int i = 0; i < 100; i++) {
            buffer.put("message " + i);
            System.out.println(Thread.currentThread().getId() + "\tput message " + i);
        }
    }
}
