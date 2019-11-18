package dev.szczygiel.first;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        int bufferSize = 10;
        int processes = 5;
        int threads = processes + 2;

        var pool = Executors.newFixedThreadPool(threads);
        var pipeline = new Pipeline(bufferSize);

        pool.submit(new Producer(pipeline));
        var consumer = pool.submit(new Consumer(pipeline, processes));

        for (int i = 0; i < processes; ++i) {
            pool.submit(new Process(pipeline, i));
        }

        try {
            consumer.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        pool.shutdown();
    }
}
