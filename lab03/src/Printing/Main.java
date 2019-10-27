package Printing;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        PrinterMonitor monitor = new PrinterMonitor(5);

        int clients = 10;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < clients; ++i) {
            threads.add(new Thread(new PrintingClient(i, monitor)));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}
