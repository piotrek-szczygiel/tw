package dev.szczygiel;

import java.util.Random;
import java.util.concurrent.Semaphore;

class Simple implements Runnable {
    private final Semaphore lock = new Semaphore(1);
    private final Semaphore agent = new Semaphore(0);
    private final Semaphore matches = new Semaphore(0);
    private final Semaphore paper = new Semaphore(0);
    private final Semaphore tobacco = new Semaphore(0);

    private final Random r = new Random();

    public static void main(String[] args) {
        Simple simple = new Simple();
        simple.run();
    }

    @Override
    public void run() {
        Thread agentThread = new Thread(this::agent);
        Thread matchesThread = new Thread(this::smokerMatches);
        Thread paperThread = new Thread(this::smokerPaper);
        Thread tobaccoThread = new Thread(this::smokerTobacco);

        matchesThread.start();
        paperThread.start();
        tobaccoThread.start();
        agentThread.start();

        try {
            agentThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void agent() {
        try {
            while (true) {
                lock.acquire();
                int rand = r.nextInt(3);

                if (rand == 0) {
                    matches.release();
                } else if (rand == 1) {
                    paper.release();
                } else if (rand == 2) {
                    tobacco.release();
                }

                lock.release();
                agent.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerMatches() {
        try {
            while (true) {
                matches.acquire();
                lock.acquire();
                System.out.println("Match smoker picking up paper and tobacco");
                Thread.sleep(r.nextInt(1000));
                agent.release();
                lock.release();
                System.out.println("Match smoker is smoking...");
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerPaper() {
        try {
            while (true) {
                paper.acquire();
                lock.acquire();
                System.out.println("Paper smoker picking up matches and tobacco");
                Thread.sleep(r.nextInt(1000));
                agent.release();
                lock.release();
                System.out.println("Paper smoker is smoking...");
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerTobacco() {
        try {
            while (true) {
                tobacco.acquire();
                lock.acquire();
                System.out.println("Tobacco smoker picking up matches and paper");
                Thread.sleep(r.nextInt(1000));
                agent.release();
                lock.release();
                System.out.println("Tobacco smoker is smoking...");
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}