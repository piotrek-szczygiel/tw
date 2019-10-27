package Eating;

public class Client implements Runnable {
    private int pair;
    private Waiter waiter;

    Client(int pair, Waiter waiter) {
        this.pair = pair;
        this.waiter = waiter;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);

            System.out.println("Waiting for table: " + pair);
            Table table = waiter.getTable(pair);

            System.out.println("Pair is eating: " + pair);
            Thread.sleep(500);

            System.out.println("One of pair " + pair + " finished eating");
            waiter.unlockTable();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
