package dev.szczygiel.second;

import java.util.Random;

public class Producer extends Thread {
    private final Buffer buffer;
    private final Csv csv;
    private final int maxSize;
    private final Random random = new Random();
    private boolean running = true;

    Producer(Buffer buffer, Csv csv) {
        this.buffer = buffer;
        this.csv = csv;

        maxSize = buffer.size() / 2;
    }

    @Override
    public void interrupt() {
        running = false;
        super.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            int n = random.nextInt(maxSize);

            var start = System.nanoTime();
            buffer.put(n);
            var elapsed = System.nanoTime() - start;

            csv.add(n, elapsed);
        }
    }
}
