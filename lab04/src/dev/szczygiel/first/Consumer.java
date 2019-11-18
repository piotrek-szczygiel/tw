package dev.szczygiel.first;

public class Consumer implements Runnable {
    private final Pipeline pipeline;
    private final int processes;

    Consumer(Pipeline pipeline, int processes) {
        this.pipeline = pipeline;
        this.processes = processes;
    }

    @Override
    public void run() {
        var buffer = pipeline.getBuffer();

        for (int i = 0; i < buffer.length; ++i) {
            var condition = pipeline.lock(i);

            while (buffer[i] != processes) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            buffer[i] = -1;
            System.out.println("Consumer: " + i);

            condition.signal();
            pipeline.unlock(i);
        }

        System.out.println("Consumer finished");
    }
}
