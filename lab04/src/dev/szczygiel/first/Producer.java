package dev.szczygiel.first;

public class Producer implements Runnable {
    private final Pipeline pipeline;

    Producer(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void run() {
        var buffer = pipeline.getBuffer();

        for (int i = 0; i < buffer.length; ++i) {
            var condition = pipeline.lock(i);

            while (buffer[i] != -1) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            buffer[i] = 0;
            System.out.println("Producer: " + i);

            condition.signal();
            pipeline.unlock(i);
        }

        System.out.println("Producer finished");
    }
}
