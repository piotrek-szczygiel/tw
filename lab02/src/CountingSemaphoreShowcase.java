import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CountingSemaphoreShowcase {
    public static void main(String[] args) throws InterruptedException {
        Shop shop = new Shop(10);
        List<Thread> threads = new ArrayList<>();

        int numClients = 100;

        for (int i = 0; i < numClients; ++i) {
            threads.add(new Thread(new Client(shop, i)));
        }

        for (Thread t : threads) {
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }
    }
}
