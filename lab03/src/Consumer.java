public class Consumer implements Runnable {
    private BoundedBuffer buffer;
    private int id;

    Consumer(BoundedBuffer buffer, int id) {
        this.buffer = buffer;
        this.id = id;
    }

    public void run() {
        try {
            System.out.println("Consumer " + id + ": " + this.buffer.take());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
