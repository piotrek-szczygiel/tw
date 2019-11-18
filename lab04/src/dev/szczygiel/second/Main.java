package dev.szczygiel.second;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        long duration = 1000;
        var producers = List.of(10, 100, 1000);
        var buffers = List.of(BufferType.NAIVE, BufferType.CLEVER);
        var sizes = List.of(1000, 10_000, 100_000);

        for (var b : buffers)
            for (var p : producers)
                for (var s : sizes)
                    run(b, s, p, duration);
    }

    private static Buffer createBuffer(BufferType type, int size) {
        if (type == BufferType.NAIVE) {
            return new BufferNaive(size);
        } else {
            return new BufferClever(size);
        }
    }

    private static void run(BufferType type, int size, int numProducers, long duration)
            throws IOException, InterruptedException {

        var file = String.format("%s_%d_%d.csv", type == BufferType.NAIVE ? "naive" : "clever", size, numProducers);
        var csvProducer = new Csv("csv/producer_" + file);
        var csvConsumer = new Csv("csv/consumer_" + file);

        System.out.println("Generating for " + file);

        var buffer = createBuffer(type, size);

        var threads = new ArrayList<Thread>();

        for (int i = 0; i < numProducers; ++i) {
            var t = new Producer(buffer, csvProducer);
            t.start();
            threads.add(t);
        }

        for (int i = 0; i < numProducers; ++i) {
            var t = new Consumer(buffer, csvConsumer);
            t.start();
            threads.add(t);
        }

        Thread.sleep(duration);

        for (var thread : threads) {
            thread.interrupt();
            thread.join();
        }

        csvProducer.save();
        csvConsumer.save();
    }
}
