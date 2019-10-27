public class Producer implements Runnable {
    private BoundedBuffer buffer;
    private int id;

    Producer(BoundedBuffer buffer, int id) {
        this.buffer = buffer;
        this.id = id;
    }

    public void run() {
        for (int i = 0; i < 10; ++i) {
            try {
                this.buffer.put("Message from " + id + ": " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}