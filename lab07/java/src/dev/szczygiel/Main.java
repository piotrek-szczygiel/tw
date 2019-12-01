package dev.szczygiel;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

class Fork {
    private ReentrantLock lock = new ReentrantLock();

    void acquire() {
        lock.lock();
    }

    void release() {
        lock.unlock();
    }

    void acquireWith(Fork other) throws InterruptedException {
        boolean locked = false;
        while (!locked) {
            lock.lock();
            locked = other.lock.tryLock();

            if (!locked) {
                lock.unlock();
            }
        }
    }
}

class Conductor {
    private Semaphore semaphore;

    Conductor(int clients) {
        semaphore = new Semaphore(clients - 1);
    }

    void acquire() throws InterruptedException {
        semaphore.acquire();
    }

    void release() {
        semaphore.release();
    }
}

class Philosopher {
    List<Long> timesAsym = new ArrayList<>();
    List<Long> timesConductor = new ArrayList<>();
    List<Long> timesBoth = new ArrayList<>();
    private int id;
    private Fork leftFork;
    private Fork rightFork;
    private Random rand = new Random();

    Philosopher(int id, Fork leftFork, Fork rightFork) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
    }

    void startNaive(int iterations) throws InterruptedException {
        for (int i = 0; i < iterations; ++i) {
            System.out.println("Philosopher " + id + " awaiting left fork...");
            leftFork.acquire();

            System.out.println("Philosopher " + id + " awaiting right fork...");
            rightFork.acquire();

            System.out.println("Philosopher " + id + " is eating...");
            Thread.sleep(rand.nextInt(1000));

            leftFork.release();
            rightFork.release();

            System.out.println("Philosopher " + id + " finished eating.");
            Thread.sleep(rand.nextInt(1000));
        }
    }

    void startAsym(int iterations) throws InterruptedException {
        for (int i = 0; i < iterations; ++i) {
            long start = System.currentTimeMillis();

            if (id % 2 == 0) {
                System.out.println("Philosopher " + id + " awaiting left fork...");
                leftFork.acquire();

                System.out.println("Philosopher " + id + " awaiting right fork...");
                rightFork.acquire();
            } else {
                System.out.println("Philosopher " + id + " awaiting right fork...");
                rightFork.acquire();

                System.out.println("Philosopher " + id + " awaiting left fork...");
                leftFork.acquire();
            }

            timesAsym.add(System.currentTimeMillis() - start);

            System.out.println("Philosopher " + id + " is eating...");
            Thread.sleep(rand.nextInt(1000));

            leftFork.release();
            rightFork.release();

            System.out.println("Philosopher " + id + " finished eating.");
            Thread.sleep(rand.nextInt(1000));
        }
    }

    void startConductor(int iterations, Conductor conductor) throws InterruptedException {
        for (int i = 0; i < iterations; ++i) {
            long start = System.currentTimeMillis();

            System.out.println("Philosopher " + id + " awaiting conductor permission...");
            conductor.acquire();

            if (id % 2 == 0) {
                System.out.println("Philosopher " + id + " awaiting left fork...");
                leftFork.acquire();

                System.out.println("Philosopher " + id + " awaiting right fork...");
                rightFork.acquire();
            } else {
                System.out.println("Philosopher " + id + " awaiting right fork...");
                rightFork.acquire();

                System.out.println("Philosopher " + id + " awaiting left fork...");
                leftFork.acquire();
            }

            timesConductor.add(System.currentTimeMillis() - start);

            System.out.println("Philosopher " + id + " is eating...");
            Thread.sleep(rand.nextInt(1000));

            leftFork.release();
            rightFork.release();

            conductor.release();

            System.out.println("Philosopher " + id + " finished eating.");
            Thread.sleep(rand.nextInt(1000));
        }
    }

    void startBoth(int iterations) throws InterruptedException {
        for (int i = 0; i < iterations; ++i) {
            long start = System.currentTimeMillis();

            System.out.println("Philosopher " + id + " awaiting both forks...");
            leftFork.acquireWith(rightFork);

            timesBoth.add(System.currentTimeMillis() - start);

            System.out.println("Philosopher " + id + " is eating...");
            Thread.sleep(rand.nextInt(1000));

            leftFork.release();
            rightFork.release();

            System.out.println("Philosopher " + id + " finished eating.");
            Thread.sleep(rand.nextInt(1000));
        }
    }
}

class Main {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        int iterations = 5;

        for (int n : List.of(5, 20, 100)) {
            System.out.println("Running test for " + n + " philosophers");

            List<Fork> forks = new ArrayList<>();
            List<Philosopher> philosophers = new ArrayList<>();

            for (int i = 0; i < n; ++i) {
                forks.add(new Fork());
            }

            for (int i = 0; i < n; ++i) {
                Fork leftFork = forks.get(i % forks.size());
                Fork rightFork = forks.get((i + 1) % forks.size());

                philosophers.add(new Philosopher(i, leftFork, rightFork));
            }

            runAsym(philosophers, iterations);
            runConductor(philosophers, iterations);
            runBoth(philosophers, iterations);

            List<Long> timesAsym = new ArrayList<>();
            List<Long> timesConductor = new ArrayList<>();
            List<Long> timesBoth = new ArrayList<>();

            for (Philosopher p : philosophers) {
                timesAsym.addAll(p.timesAsym);
                timesConductor.addAll(p.timesConductor);
                timesBoth.addAll(p.timesBoth);
            }

            double avgAsym = average(timesAsym);
            double avgConductor = average(timesConductor);
            double avgBoth = average(timesBoth);

            String results = "Asym: " + avgAsym + "\n"
                    + "Conductor: " + avgConductor + "\n"
                    + "Both: " + avgBoth + "\n";

            System.out.println(results);

            try (PrintWriter out = new PrintWriter("philosophers_" + n + ".txt")) {
                out.println(results);
            }
        }
    }

    private static double average(List<Long> array) {
        return array.stream().mapToDouble(a -> a).average().orElseThrow();
    }

    private static void runNaive(List<Philosopher> philosophers, int iterations) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (Philosopher p : philosophers) {
            threads.add(new Thread(() -> {
                try {
                    p.startNaive(iterations);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    private static void runAsym(List<Philosopher> philosophers, int iterations) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (Philosopher p : philosophers) {
            threads.add(new Thread(() -> {
                try {
                    p.startAsym(iterations);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    private static void runConductor(List<Philosopher> philosophers, int iterations) throws InterruptedException {
        Conductor conductor = new Conductor(philosophers.size());

        List<Thread> threads = new ArrayList<>();

        for (Philosopher p : philosophers) {
            threads.add(new Thread(() -> {
                try {
                    p.startConductor(iterations, conductor);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    private static void runBoth(List<Philosopher> philosophers, int iterations) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (Philosopher p : philosophers) {
            threads.add(new Thread(() -> {
                try {
                    p.startBoth(iterations);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}