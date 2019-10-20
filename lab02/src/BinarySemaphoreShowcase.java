public class BinarySemaphoreShowcase {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter(new BinarySemaphore());

        Thread incrementThread = new Thread(() -> {
            for (int i = 0; i < 1_000_000; ++i) {
                counter.inc();
            }
        });

        Thread decrementThread = new Thread(() -> {
            for (int i = 0; i < 1_000_000; ++i) {
                counter.dec();
            }
        });

        incrementThread.start();
        decrementThread.start();

        incrementThread.join();
        decrementThread.join();

        System.out.println(counter.getValue());
    }
}
