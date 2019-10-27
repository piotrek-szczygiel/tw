package Eating;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int pairs = 10;

        Waiter waiter = new Waiter(pairs);

        List<Thread> clientsThreads = new ArrayList<>();
        for (int i = 0; i < pairs; ++i) {
            clientsThreads.add(new Thread(new Client(i, waiter)));
            clientsThreads.add(new Thread(new Client(i, waiter)));
        }

        for (Thread thread : clientsThreads) {
            thread.start();
        }

        for (Thread thread : clientsThreads) {
            thread.join();
        }
    }
}
