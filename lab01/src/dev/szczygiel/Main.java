package dev.szczygiel;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Counter counter = new Counter();
        int ITERATIONS = 1000000;

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; ++i) {
                counter.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < ITERATIONS; ++i) {
                counter.decrement();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Error while joining threads");
            e.printStackTrace();
        }

        System.out.println(counter.getValue());

        List<Thread> threads = new ArrayList<>();

        Buffer b = new Buffer();
        for (int i = 0; i < 100; ++i) {
            Thread producer = new Thread(() -> {
                Producer p = new Producer(b);
                p.run();
            });
            Thread consumer = new Thread(() -> {
                Consumer c = new Consumer(b);
                c.run();
            });

            producer.start();
            consumer.start();

            threads.add(producer);
            threads.add(consumer);
        }

        try {
            for (Thread t : threads) {
                t.join();
            }
        } catch (InterruptedException e) {
            System.out.println("Error while joining threads");
            e.printStackTrace();
        }
    }
}
