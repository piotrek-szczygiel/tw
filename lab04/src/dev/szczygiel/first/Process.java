package dev.szczygiel.first;

import java.util.Random;

public class Process implements Runnable {
    private final Pipeline pipeline;
    private final int id;

    private final Random random = new Random();

    Process(Pipeline pipeline, int id) {
        this.pipeline = pipeline;
        this.id = id;
    }

    @Override
    public void run() {
        var buffer = pipeline.getBuffer();

        for (int i = 0; i < buffer.length; ++i) {
            var condition = pipeline.lock(i);
            try {

                while (buffer[i] != id) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("Process " + id + ": " + i);

                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                buffer[i] = id + 1;
                condition.signal();
            } finally {
                pipeline.unlock(i);
            }
        }

        System.out.println("Process " + id + " finished");
    }
}
