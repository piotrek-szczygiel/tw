package dev.szczygiel;

import java.util.Random;
import java.util.concurrent.Semaphore;

class AcquireMultiple implements Runnable {
    private final Semaphore agent = new Semaphore(1);
    private final Semaphore matches = new Semaphore(0);
    private final Semaphore paper = new Semaphore(0);
    private final Semaphore tobacco = new Semaphore(0);

    private final Random r = new Random();

    public static void main(String[] args) {
        AcquireMultiple acquireMultiple = new AcquireMultiple();
        acquireMultiple.run();
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

    private void acquireBoth(Semaphore a, Semaphore b) throws InterruptedException {
        int backoff = 1;
        while (true) {
            a.acquire();
            if (b.tryAcquire()) {
                return;
            }
            a.release();
            Thread.sleep(backoff *= 2);
        }
    }

    private void agent() {
        try {
            while (true) {
                int rand = r.nextInt(3);

                agent.acquire();

                if (rand == 0) {
                    paper.release();
                    tobacco.release();
                } else if (rand == 1) {
                    matches.release();
                    tobacco.release();
                } else if (rand == 2) {
                    matches.release();
                    paper.release();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerMatches() {
        try {
            while (true) {
                acquireBoth(paper, tobacco);
                System.out.println("Match smoker acquired paper and tobacco");
                System.out.println("Match smoker is smoking...");
                agent.release();
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerPaper() {
        try {
            while (true) {
                acquireBoth(matches, tobacco);
                System.out.println("Paper smoker acquired matches and tobacco");
                System.out.println("Paper smoker is smoking...");
                agent.release();
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void smokerTobacco() {
        try {
            while (true) {
                acquireBoth(matches, paper);
                System.out.println("Tobacco smoker acquired matches and paper");
                System.out.println("Tobacco smoker is smoking...");
                agent.release();
                Thread.sleep(r.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
