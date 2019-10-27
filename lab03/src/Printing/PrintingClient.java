package Printing;

public class PrintingClient implements Runnable {
    private final PrinterMonitor monitor;
    private int id;

    PrintingClient(int id, PrinterMonitor monitor) {
        this.id = id;
        this.monitor = monitor;
    }

    @Override
    public void run() {
        try {
            Printer p = monitor.reserve();
            p.print("Hello, I'm client " + id);
            monitor.release(p);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
