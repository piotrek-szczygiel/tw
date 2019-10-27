import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        BoundedBuffer buffer = new BoundedBuffer(5);

        int consumers = 50;
        int producers = 5;

        List<Thread> consumerThreads = new ArrayList<>();
        for (int i = 0; i < consumers; ++i) {
            consumerThreads.add(new Thread(new Consumer(buffer, i)));
        }

        List<Thread> producerThreads = new ArrayList<>();
        for (int i = 0; i < producers; ++i) {
            producerThreads.add(new Thread(new Producer(buffer, i)));
        }

        for (Thread thread : consumerThreads) {
            thread.start();
        }

        for (Thread thread : producerThreads) {
            thread.start();
        }

        try {
            for (Thread thread : consumerThreads) {
                thread.join();
            }

            for (Thread thread : producerThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
